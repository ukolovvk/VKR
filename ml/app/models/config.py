from pydantic import BaseModel, Field
from typing import Literal


class LoggingConfig(BaseModel):
    level: Literal["DEBUG", "INFO", "WARN", "ERROR"]


class S3Cfg(BaseModel):
    url: str
    user: str
    password: str
    audio_bucket: str
    reports_bucket: str


class PostgresCfg(BaseModel):
    host: str
    port: int = Field(gt=0, le=65535)
    user: str
    password: str
    database: str
    audio_table_name: str


class EurekaCfg(BaseModel):
    url: str
    app_name: str


class AppConfig(BaseModel):
    port: int = Field(gt=0, le=65535)
    log: LoggingConfig
    s3: S3Cfg
    postgres: PostgresCfg
    eureka: EurekaCfg
