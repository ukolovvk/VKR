from pydantic import BaseModel


class AnalyzeAudioReq(BaseModel):
    s3_filename: str
