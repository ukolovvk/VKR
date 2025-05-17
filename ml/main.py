import uvicorn
from app.config import CFG


if __name__ == '__main__':
    uvicorn.run("app:app", host="0.0.0.0", port=CFG.port, reload=True)
