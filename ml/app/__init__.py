import logging
import sys

from app.config import CFG
from fastapi import FastAPI
from py_eureka_client import eureka_client
from app.handlers.audio import audio_router


def _setup_logger(level):
    formatter = logging.Formatter(
        '%(asctime)s - %(name)s - %(levelname)s - %(message)s'
    )
    console_handler = logging.StreamHandler(sys.stdout)
    console_handler.setFormatter(formatter)
    root_logger = logging.getLogger()
    root_logger.setLevel(level)
    root_logger.addHandler(console_handler)


_setup_logger(CFG.log.level)

app = FastAPI()
app.include_router(audio_router)

eureka_client.init(
    eureka_server=CFG.eureka.url,
    app_name=CFG.eureka.app_name,
    instance_port=CFG.port
)