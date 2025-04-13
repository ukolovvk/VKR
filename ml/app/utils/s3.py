import aioboto3
import logging
from app.config import CFG
from io import BytesIO


logger = logging.getLogger(__name__)


async def download_file_from_s3(bucket: str, file_uuid: str) -> bytes:
    async with aioboto3.Session().client(
            "s3",
            endpoint_url=CFG.s3.url,
            aws_access_key_id=CFG.s3.user,
            aws_secret_access_key=CFG.s3.password,
            config=aioboto3.session.AioConfig(
                signature_version='s3v4',
                s3={'addressing_style': 'path'}
            )
    ) as s3:
        try:
            resp = await s3.get_object(Bucket=bucket, Key=file_uuid)
            return await resp["Body"].read()
        except s3.exceptions.NoSuchKey:
            raise FileNotFoundError(f"file not found in bucket: {file_uuid}")
        except Exception as ex:
            logger.exception("failed to download file from S3", exc_info=ex)
            raise


async def upload_file_to_s3(bucket: str, file_name: str, file: BytesIO):
    async with aioboto3.Session().client(
            "s3",
            endpoint_url=CFG.s3.url,
            aws_access_key_id=CFG.s3.user,
            aws_secret_access_key=CFG.s3.password,
    ) as s3:
        try:
            await s3.upload_fileobj(file, bucket, file_name)
        except Exception as ex:
            logger.exception("failed to upload file from S3", exc_info=ex)
            raise
