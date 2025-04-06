from app import pgpool
from app.config import CFG
from app.utils.status import AnalyzeStatus


async def set_report_link(audio_uuid: str, report_link: str):
    async with pgpool.acquire() as conn:
        await conn.execute(f'''
            UPDATE {CFG.postgres.audio_table_name}
            SET analyze_finished = (EXTRACT(EPOCH FROM clock_timestamp()) * 1000)::bigint,
            status = '{AnalyzeStatus.finished.value}',
            report_link = '{report_link}'
            WHERE s3_uuid = '{audio_uuid}'
        ''')


async def set_status_in_progress(audio_uuid: str):
    async with pgpool.acquire() as conn:
        await conn.execute(f'''
            UPDATE {CFG.postgres.audio_table_name}
            SET analyze_started = (EXTRACT(EPOCH FROM clock_timestamp()) * 1000)::bigint,
            status = '{AnalyzeStatus.in_progress.value}'
        ''')
