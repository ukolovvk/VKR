import enum


class AnalyzeStatus(enum.Enum):
    uploaded = "uploaded"
    in_progress = "in_progress"
    finished = "finished"
