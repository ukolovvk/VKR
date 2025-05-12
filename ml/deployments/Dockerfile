FROM python:3.12-slim

RUN apt-get update && apt-get install -y netcat-traditional libgomp1

WORKDIR /app

RUN mkdir -p /app/app /etc/ml

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY ./app ./app
COPY ./main.py .
COPY ./deployments/config.yaml /etc/ml/config.yaml
COPY ./yamnet_class_map.csv .

CMD ["python", "main.py", "-c", "/etc/ml/config.yaml"]