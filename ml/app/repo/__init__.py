import asyncpg
from app.config import CFG

_pgpool = None


async def init_pgpool():
    global _pgpool
    if _pgpool is None:
        _pgpool = await asyncpg.create_pool(
            f"postgresql://{CFG.postgres.user}:{CFG.postgres.password}@{CFG.postgres.host}:{CFG.postgres.port}/{CFG.postgres.database}",
            min_size=3,
            max_size=30
        )
    return _pgpool


async def get_pgpool():
    if _pgpool is None:
        raise RuntimeError("pg pool isn't initialized")
    return _pgpool


async def close_pgpool():
    global _pgpool
    if _pgpool is not None:
        await _pgpool.close()
        _pgpool = None
