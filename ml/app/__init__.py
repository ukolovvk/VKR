import logging
import sys

import asyncpg

from app.config import CFG
from fastapi import FastAPI
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

pgpool = asyncpg.create_pool(
    f"postgresql://{CFG.postgres.user}:{CFG.postgres.password}@{CFG.postgres.host}:{CFG.postgres.port}/{CFG.postgres.database}",
    min_size=3,
    max_size=30
)

app = FastAPI()
app.include_router(audio_router)
