# syntax=docker/dockerfile:1.7
FROM python:3.11-slim AS base

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    POETRY_VERSION=1.8.3 \
    APP_HOME=/app

WORKDIR ${APP_HOME}

COPY inference/requirements.txt ./requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

COPY inference/app ./app
COPY common ./common

ENV PYTHONPATH="${APP_HOME}"

EXPOSE 9090

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "9090"]
