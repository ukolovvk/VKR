FROM python:3.9-slim

RUN apt-get update && apt-get install -y netcat-traditional

WORKDIR /app

RUN mkdir -p /app/app /etc/ml

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY ./app ./app
COPY ./main.py .
COPY ./deployments/config.yaml /etc/ml/config.yaml

CMD ["python", "main.py", "-c", "/etc/ml/config.yaml"]