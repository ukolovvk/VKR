import aioboto3
import logging
from app.config import CFG
from botocore.exceptions import ClientError
from io import BytesIO


logger = logging.getLogger(__name__)


async def download_file_from_s3(bucket: str, file_uuid: str) -> bytes:
    async with aioboto3.Session().client(
            "s3",
            endpoint_url=CFG.s3.url,
            aws_access_key_id=CFG.s3.user,
            aws_secret_access_key=CFG.s3.password,
    ) as s3:
        resp = await s3.get_object(Bucket=bucket, Key=file_uuid)
        return await resp["Body"].read()


async def upload_file_to_s3(bucket: str, file_name: str, file: BytesIO):
    async with aioboto3.Session().client(
            "s3",
            endpoint_url=CFG.s3.url,
            aws_access_key_id=CFG.s3.user,
            aws_secret_access_key=CFG.s3.password,
    ) as s3:
        await s3.upload_fileobj(file, bucket, file_name)
