import asyncio
import logging
from fastapi import APIRouter, BackgroundTasks, Query
from app.utils.api import response_ok, response_bad_req
from app.analyzers.ml_analyzer import analyze_audio
from app.config import CFG
from app.models.audio import AnalyzeAudioReq

audio_router = APIRouter(prefix="/audio/v1")

logger = logging.getLogger(__name__)


@audio_router.post("/analyze")
async def analyze(
        audio_req: AnalyzeAudioReq,
        background_tasks: BackgroundTasks,
        model: str = Query()
):
    if model not in {"yamnet", "ast"}:
        return response_bad_req(f"invalid model name: {model}")
    background_tasks.add_task(analyze_audio, CFG.s3.audio_bucket, CFG.s3.reports_bucket, audio_req.s3_filename, model)
    return response_ok("Audio will be analyzed")
