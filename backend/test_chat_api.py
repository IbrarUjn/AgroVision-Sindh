# test_chat_api.py
# Tests for AgroVision AI Farmer Assistant backend integration

import pytest
import json
import sys
import os

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from app import app


@pytest.fixture
def client():
    app.config["TESTING"] = True
    with app.test_client() as client:
        yield client


def test_chat_empty_message(client):
    """POST /chat with empty message returns 400"""
    res = client.post("/chat", json={"message": ""})
    assert res.status_code == 400
    data = res.get_json()
    assert data["status"] == "error"


def test_chat_general_english(client):
    """POST /chat general agriculture question in English"""
    res = client.post("/chat", json={
        "message": "Why are my wheat leaves turning yellow?",
        "language": "en"
    })
    assert res.status_code == 200
    data = res.get_json()
    assert data["status"] == "ok"
    assert "reply" in data
    assert len(data["reply"]) > 10
    print("\n[Chat EN Reply Preview]:\n", data["reply"][:150])


def test_chat_prediction_grounded_urdu(client):
    """POST /chat with active tomato early blight prediction in Urdu"""
    pred_context = {
        "class_name": "Tomato___Early_blight",
        "crop_en": "Tomato",
        "crop_ur": "ٹماٹر",
        "crop_sd": "ٽماٽو",
        "disease_en": "Tomato Early Blight",
        "disease_ur": "ٹماٹر کا اگیتا جھلساؤ",
        "disease_sd": "ٽماٽي جو اڳواٽ بيماري",
        "confidence": 92.4,
        "advice_en": "Remove lower infected leaves. Apply copper fungicide.",
        "advice_ur": "نچلے متاثرہ پتے ہٹا دیں۔ تانبے کا فنگسائڈ استعمال کریں۔",
        "advice_sd": "هيٺيان بيمار پن هٽايو. ڪاپر فنگيسائيڊ استعمال ڪريو."
    }
    res = client.post("/chat", json={
        "message": "یہ بیماری کیوں لگی ہے اور میں اس سے کیسے بچاؤں؟",
        "language": "ur",
        "prediction_context": pred_context,
        "history": [
            {"role": "user", "text": "میرے پودے پر کیا مسئلہ ہے؟"}
        ]
    })
    assert res.status_code == 200
    data = res.get_json()
    assert data["status"] == "ok"
    assert "reply" in data
    assert "ٹماٹر" in data["reply"] or "جھلساؤ" in data["reply"] or len(data["reply"]) > 20
    print("\n[Chat UR Reply Preview]:\n", data["reply"][:150])


def test_chat_prediction_grounded_sindhi(client):
    """POST /chat with active prediction in Sindhi"""
    pred_context = {
        "class_name": "Tomato___Early_blight",
        "crop_en": "Tomato",
        "crop_ur": "ٹماٹر",
        "crop_sd": "ٽماٽو",
        "disease_en": "Tomato Early Blight",
        "disease_ur": "ٹماٹر کا اگیتا جھلساؤ",
        "disease_sd": "ٽماٽي جو اڳواٽ بيماري",
        "confidence": 88.5,
        "advice_en": "Remove infected leaves.",
        "advice_ur": "متاثرہ پتے ہٹا دیں۔",
        "advice_sd": "هيٺيان بيمار پن هٽايو ۽ متوازن پاڻي ڏيو."
    }
    res = client.post("/chat", json={
        "message": "هن بيماري کان ڪيئن بچجي؟",
        "language": "sd",
        "prediction_context": pred_context
    })
    assert res.status_code == 200
    data = res.get_json()
    assert data["status"] == "ok"
    assert "reply" in data
    print("\n[Chat SD Reply Preview]:\n", data["reply"][:150])
