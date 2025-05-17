import asyncio
from concurrent.futures import ThreadPoolExecutor
import torch
import torchaudio
from app.utils.s3 import download_file_from_s3, upload_file_to_s3
from app.repo.audio_repo import set_report_link, set_status_in_progress, set_status_finished
import pandas as pd
import librosa
from app.config import CFG
from io import BytesIO
import numpy as np
from . import yamnet_model, class_names, ast_model, ast_feature_extractor


YAMNET_THRESHOLD = 0.6
YAMNET_SAMPLE_RATE = 16000


class AudioAnalyzeException(Exception):
    pass


async def analyze_audio(audio_bucket, reports_bucket, s3_filename, model):
    audio_data = await download_file_from_s3(audio_bucket, s3_filename)
    await set_status_in_progress(s3_filename)
    if model == 'ast':
        result = await _analyze_ast(audio_data)
    else:
        result = await _yamnet_analyze(audio_data)
    await set_status_finished(s3_filename)
    df = pd.DataFrame(result, columns=["Time", "Event", "Confidence"])
    excel_buffer = BytesIO()
    with pd.ExcelWriter(excel_buffer, engine="openpyxl") as writer:
        df.to_excel(writer, index=False)
    excel_buffer.seek(0)
    report_file_name = f"report_{s3_filename}"
    await upload_file_to_s3(reports_bucket, report_file_name, excel_buffer)
    report_link = f"{CFG.s3.url}/{reports_bucket}/{report_file_name}".replace("minio", "localhost", 1)
    await set_report_link(s3_filename, report_link)


async def _yamnet_analyze(audio_bytes):
    loop = asyncio.get_event_loop()
    executor = ThreadPoolExecutor()
    try:
        audio, sr = await loop.run_in_executor(
            executor,
            lambda: librosa.load(BytesIO(audio_bytes), sr=YAMNET_SAMPLE_RATE, mono=True)
        )
    except Exception as ex:
        print(f"failed to load audio bytes by librosa: {ex}")
        raise AudioAnalyzeException(ex)
    if sr != YAMNET_SAMPLE_RATE:
        raise AudioAnalyzeException(f"invalid sample rate {sr} after loading (must be 16kHz)")
    if audio.dtype != np.float32:
        raise AudioAnalyzeException("audio type must be float32 after loading")
    audio_duration_ms = (len(audio) / YAMNET_SAMPLE_RATE) * 1000
    scores, embeddings, spectrogram = yamnet_model(audio)  # make prediction
    result = []
    current_classes = {}  # map[class](start_ms, end_ms, max_probability, is_predicted_in_current_iteration)
    for i, window_scores in enumerate(scores):
        window_start = i * 480  # 480 ms is stride for 960 ms window: 00:00-00:960 -> 00:480-01:440
        window_end = min(audio_duration_ms, window_start + 960)  # handle right border
        class_indexes = [idx for idx, score in enumerate(window_scores) if score >= YAMNET_THRESHOLD]
        for cls in current_classes:
            current_classes[cls][3] = False
        for idx in class_indexes:
            class_probability = float(window_scores[idx].numpy())
            class_name = class_names[idx]
            if class_name not in current_classes:
                current_classes[class_name] = [window_start, window_end, class_probability, True]
                continue
            current_classes[class_name][1] = window_end
            current_classes[class_name][2] = max(current_classes[class_name][2], class_probability)
            current_classes[class_name][3] = True
        to_remove = [name for name, data in current_classes.items() if not data[3]]
        for name in to_remove:
            result.append(_format_yamnet(*current_classes[name][:3], name))
            del current_classes[name]
    result.extend(_format_yamnet(*data[:3], name) for name, data in current_classes.items())
    return result


def _format_yamnet(start_ms, end_ms, prob, class_name):
    start_mins = start_ms // 60000
    start = start_ms % 60000
    start_sec = start // 1000
    start_msec = start % 1000

    end_mins = end_ms // 60000
    end = end_ms % 60000
    end_sec = end // 1000
    end_msec = end % 1000

    return (
        f"{int(start_mins)}:{int(start_sec)}:{int(start_msec):03d}-"
        f"{int(end_mins)}:{int(end_sec)}:{int(end_msec):03d}",
        class_name,
        round(prob, 2)
    )


async def _analyze_yamnet_seq(audio_bytes):
    try:
        audio, sr = librosa.load(BytesIO(audio_bytes), sr=16000, mono=True)
    except Exception as e:
        print(f"failed to load audio: {e}")
        raise AudioAnalyzeException(e)
    if sr != 16000:
        raise AudioAnalyzeException("invalid sample rate after loading")
    if audio.dtype != np.float32:
        raise AudioAnalyzeException("audio type must be float32")
    scores, embeddings, spectrogram = yamnet_model(audio)
    result = []
    current_class = None
    start_time = 0
    end_time = 0
    max_conf = 0.0
    for i, window_scores in enumerate(scores):
        top_class_idx = np.argmax(window_scores)
        confidence = float(window_scores[top_class_idx].numpy())
        if confidence >= 0.5:
            timestamp = i * 0.96  # Время в секундах (каждое окно = 0.96 сек)
            class_name = class_names[top_class_idx]
            if class_name == current_class:
                max_conf = max(max_conf, confidence)
                end_time = timestamp
            else:
                if current_class is not None:
                    mins_start = int(start_time // 60)
                    secs_start = int(start_time % 60)
                    mins_end = int(end_time // 60)
                    secs_end = int(end_time % 60)
                    result.append((f"{mins_start:02d}:{secs_start:02d}-{mins_end:02d}:{secs_end:02d}", current_class, max_conf))
                current_class = class_name
                start_time = timestamp
                end_time = timestamp
                max_conf = confidence
    if current_class is not None and start_time != end_time:
        mins_start = int(start_time // 60)
        secs_start = int(start_time % 60)
        mins_end = int(end_time // 60)
        secs_end = int(end_time % 60)
        result.append((f"{mins_start:02d}:{secs_start:02d}-{mins_end:02d}:{secs_end:02d}", current_class, max_conf))
    return result


async def _analyze_ast(audio_bytes, window_size=2.0, stride=0.5, confidence_threshold=0.2):
    waveform, sr = torchaudio.load(BytesIO(audio_bytes))  # тензор размерностью (количество каналов, количество сэмплов)
    if waveform.shape[0] > 1:
        waveform = waveform.mean(dim=0, keepdim=True)
    target_sr = ast_feature_extractor.sampling_rate
    min_samples = 512
    if waveform.size(1) < min_samples:
        pad_size = min_samples - waveform.size(1)
        waveform = torch.nn.functional.pad(waveform, (0, pad_size))
    if sr != target_sr:
        waveform = torchaudio.transforms.Resample(sr, target_sr)(waveform)

    num_samples = waveform.shape[1]
    samples_per_window = int(target_sr * window_size)
    samples_stride = int(target_sr * stride)

    class_buffer = []
    current_class = None
    start_time = 0.0
    end_time = 0.0
    max_conf = 0.0
    result = []

    for start in range(0, num_samples, samples_stride):
        end = start + samples_per_window
        if end > num_samples:
            end = num_samples

        segment = waveform[:, start:end]
        if (end - start) < int(0.01 * target_sr):
            continue

        if segment.size(1) < samples_per_window:
            pad_size = samples_per_window - segment.size(1)
            segment = torch.nn.functional.pad(segment, (0, pad_size))

        inputs = ast_feature_extractor(segment.numpy().squeeze(), sampling_rate=target_sr, return_tensors="pt")
        with torch.no_grad():
            outputs = ast_model(**inputs)

        confs = torch.nn.functional.softmax(outputs.logits, dim=-1).squeeze()
        conf, pred_idx = torch.max(confs, dim=0)
        class_name = ast_model.config.id2label[pred_idx.item()]

        class_buffer.append((class_name, conf.item()))
        if len(class_buffer) > 3:
            class_buffer.pop(0)

        buf_classes = [c[0] for c in class_buffer]
        dominant_class = max(set(buf_classes), key=lambda x: buf_classes.count(x))
        avg_conf = np.mean([c[1] for c in class_buffer if c[0] == dominant_class])

        if avg_conf >= confidence_threshold:
            if dominant_class == current_class:
                max_conf = max(max_conf, avg_conf)
                end_time = end / target_sr
            else:
                if current_class is not None:
                    result.append(_format_interval(start_time, end_time, current_class, max_conf))
                current_class = dominant_class
                start_time = start / target_sr
                end_time = end / target_sr
                max_conf = avg_conf

    if current_class is not None:
        result.append(_format_interval(start_time, end_time, current_class, max_conf))

    return result


def _format_interval(start, end, cls, conf):
    return (
        f"{int(start // 60):02d}:{int(start % 60):02d}-{int(end // 60):02d}:{int(end % 60):02d}",
        cls,
        round(conf, 2)
    )
