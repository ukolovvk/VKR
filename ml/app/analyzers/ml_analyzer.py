from app.utils.s3 import download_file_from_s3, upload_file_to_s3
from app.repo.audio_repo import set_report_link, set_status_in_progress
import pandas as pd
from app.config import CFG
from io import BytesIO


_mock_report = {
    "00:03-00:10": "dog was barking",
    "00:14-00:17": "car's signal",
    "00:15-00:45": "human's speech"
}


async def analyze_audio(audio_bucket, reports_bucket, file_uuid):
    audio_data = await download_file_from_s3(audio_bucket, file_uuid)
    await set_status_in_progress(file_uuid)
    # TODO ADD ANALYZING
    df = pd.DataFrame(list(_mock_report.items()), columns=["Time", "Event"])
    excel_buffer = BytesIO()
    with pd.ExcelWriter(excel_buffer, engine="openpyxl") as writer:
        df.to_excel(writer, index=False)
    excel_buffer.seek(0)
    report_file_name = f"report_{file_uuid}"
    await upload_file_to_s3(reports_bucket, report_file_name, excel_buffer)
    report_link = f"http://{CFG.s3.url}/{reports_bucket}/{report_file_name}"
    await set_report_link(file_uuid, report_link)

