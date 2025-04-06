import logging
from fastapi import APIRouter
from app.analyzers.ml_analyzer import analyze_audio
from app.config import CFG
from app.models.audio import AnalyzeAudioReq

audio_router = APIRouter(prefix="/audio/v1")

logger = logging.getLogger(__name__)


@audio_router.post("/analyze")
async def analyze(audio_req: AnalyzeAudioReq):
    await analyze_audio(CFG.s3.audio_bucket, CFG.s3.reports_bucket, audio_req.s3_uuid)
