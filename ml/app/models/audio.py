from pydantic import BaseModel


class AnalyzeAudioReq(BaseModel):
    s3_uuid: str
