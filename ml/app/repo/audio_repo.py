from app.config import CFG
from app.utils.status import AnalyzeStatus
from . import get_pgpool


async def set_report_link(audio_uuid: str, report_link: str):
    pgpool = await get_pgpool()
    async with pgpool.acquire() as conn:
        await conn.execute(f'''
            UPDATE {CFG.postgres.audio_table_name}
            SET report_link = '{report_link}'
            WHERE uuid = '{audio_uuid}'
        ''')


async def set_status_in_progress(audio_uuid: str):
    pgpool = await get_pgpool()
    async with pgpool.acquire() as conn:
        await conn.execute(f'''
            UPDATE {CFG.postgres.audio_table_name}
            SET analyze_started = (EXTRACT(EPOCH FROM clock_timestamp()) * 1000)::bigint,
            status = '{AnalyzeStatus.in_progress.value}'
            WHERE uuid = '{audio_uuid}'
        ''')


async def set_status_finished(audio_uuid: str):
    pgpool = await get_pgpool()
    async with pgpool.acquire() as conn:
        await conn.execute(f'''
            UPDATE {CFG.postgres.audio_table_name}
            SET analyze_finished = (EXTRACT(EPOCH FROM clock_timestamp()) * 1000)::bigint,
            status = '{AnalyzeStatus.finished.value}'
            WHERE uuid = '{audio_uuid}'
        ''')
