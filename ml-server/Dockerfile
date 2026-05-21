FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt \
    --index-url https://download.pytorch.org/whl/cpu

COPY main.py .

# 이미지 빌드 시 모델 미리 다운로드
RUN python -c "\
from transformers import AutoTokenizer, AutoModelForSequenceClassification; \
AutoTokenizer.from_pretrained('hun3359/klue-bert-base-sentiment'); \
AutoModelForSequenceClassification.from_pretrained('hun3359/klue-bert-base-sentiment')"

EXPOSE 8000

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
