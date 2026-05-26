from fastapi import FastAPI
from pydantic import BaseModel, Field
from transformers import AutoTokenizer, AutoModelForSequenceClassification
import torch

app = FastAPI()

# 디바이스 설정
device = "cuda" if torch.cuda.is_available() else "cpu"

# 모델 로딩 (서버 시작 시 1회)
tokenizer = AutoTokenizer.from_pretrained("hun3359/klue-bert-base-sentiment")
model = AutoModelForSequenceClassification.from_pretrained("hun3359/klue-bert-base-sentiment")
model.to(device)
model.eval()

# label 매핑
label_map = model.config.id2label

class Request(BaseModel):
    text: str = Field(min_length=1, max_length=1000)

@app.post("/predict")
def predict(req: Request):
    inputs = tokenizer(req.text, return_tensors="pt", truncation=True)

    inputs = {k: v.to(device) for k, v in inputs.items()}

    with torch.no_grad():
        outputs = model(**inputs)

    probs = torch.nn.functional.softmax(outputs.logits, dim=-1)

    # Top-3 추출 후 합이 1이 되도록 정규화
    topk = torch.topk(probs, 3)
    topk_sum = topk.values[0].sum().item()

    results = []
    for i in range(3):
        idx = topk.indices[0][i].item()
        results.append({
            "label": label_map[idx],
            "score": float(topk.values[0][i].item()) / topk_sum
        })

    return {
        "text": req.text,
        "predictions": results
    }