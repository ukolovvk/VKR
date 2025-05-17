import argparse
import logging
import yaml
from app.models.config import AppConfig
from pydantic import ValidationError

logger = logging.getLogger(__name__)


def _init_config() -> AppConfig:
    parser = argparse.ArgumentParser(description="Load YAML config file")
    parser.add_argument(
        "-c", "--config",
        required=True,
        help="Path to YAML config file"
    )
    args = parser.parse_args()
    with open(args.config, "r", encoding="utf-8") as f:
        cfg_data = yaml.safe_load(f)
    try:
        return AppConfig(**cfg_data)
    except ValidationError as err:
        logger.fatal(f"failed to load config: {err}")
        raise


CFG = _init_config()
