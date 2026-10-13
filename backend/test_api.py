# AgroVision Sindh — Backend API Tests
# Run with: pytest test_api.py -v

import pytest
import json
import os
import io
import sys

# Add backend dir to path
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from app import app, CLASS_NAMES, CONFIDENCE_THRESHOLD


@pytest.fixture
def client():
    """Flask test client fixture."""
    app.config["TESTING"] = True
    with app.test_client() as client:
        yield client


# ─── Unit Tests ─────────────────────────────────────────────────────────────

def test_health_endpoint(client):
    """GET /health should return 200 with model info."""
    response = client.get("/health")
    assert response.status_code == 200
    data = json.loads(response.data)
    assert data["status"] == "ok"
    assert data["classes"] == 38
    print(f"  Health check passed: {data}")


def test_classes_endpoint(client):
    """GET /classes should return all 38 class names."""
    response = client.get("/classes")
    assert response.status_code == 200
    data = json.loads(response.data)
    assert data["count"] == 38
    assert len(data["classes"]) == 38
    assert "Tomato___Healthy" in data["classes"]
    assert "Potato___Late_blight" in data["classes"]
    print(f"  Classes endpoint returned {data['count']} classes")


def test_predict_no_image(client):
    """POST /predict without image should return 400."""
    response = client.post("/predict")
    assert response.status_code == 400
    data = json.loads(response.data)
    assert data["status"] == "error"
    print(f"  No-image error handled correctly: {data['message']}")


def test_predict_with_synthetic_green_leaf(client):
    """
    POST /predict with a synthetic solid-green 224x224 image.
    The model should return a response (likely low_confidence for a solid color),
    but the API structure must be correct regardless.
    """
    from PIL import Image
    import io

    # Create a solid green image simulating a leaf background
    img = Image.new("RGB", (224, 224), color=(34, 139, 34))
    buf = io.BytesIO()
    img.save(buf, format="JPEG")
    buf.seek(0)

    response = client.post(
        "/predict",
        data={"image": (buf, "test_leaf.jpg")},
        content_type="multipart/form-data",
    )
    assert response.status_code == 200
    data = json.loads(response.data)

    # Check required fields exist regardless of status
    assert "status" in data
    assert data["status"] in ("ok", "low_confidence")
    assert "confidence" in data
    assert "class_name" in data
    assert "crop_en" in data
    assert "disease_en" in data
    assert "advice_en" in data
    assert "advice_ur" in data
    assert "advice_sd" in data
    assert "disclaimer" in data
    assert "top3" in data
    assert len(data["top3"]) == 3

    print(f"  Prediction: {data['class_name']} ({data['confidence']:.1f}%) — status: {data['status']}")


def test_predict_response_structure_fields(client):
    """Verify all required response fields are present in a prediction."""
    from PIL import Image
    import io

    img = Image.new("RGB", (224, 224), color=(100, 180, 80))
    buf = io.BytesIO()
    img.save(buf, format="PNG")
    buf.seek(0)

    response = client.post(
        "/predict",
        data={"image": (buf, "leaf.png")},
        content_type="multipart/form-data",
    )
    assert response.status_code == 200
    data = json.loads(response.data)

    required_fields = [
        "status", "class_name", "crop_en", "crop_ur", "crop_sd",
        "disease_en", "disease_ur", "disease_sd",
        "confidence", "advice_en", "advice_ur", "advice_sd",
        "disclaimer", "top3"
    ]
    for field in required_fields:
        assert field in data, f"Missing required field: {field}"

    assert isinstance(data["confidence"], (int, float))
    assert 0 <= data["confidence"] <= 100
    print(f"  All {len(required_fields)} required fields present in response [OK]")


def test_class_names_count():
    """Verify exactly 38 class names are defined."""
    assert len(CLASS_NAMES) == 38


def test_confidence_threshold():
    """Verify confidence threshold is correctly set."""
    assert CONFIDENCE_THRESHOLD == 70.0


def test_disease_advice_coverage():
    """Verify disease_advice.py covers all expected classes."""
    from disease_advice import DISEASE_ADVICE, get_advice

    # Test a few critical classes
    test_classes = [
        "Tomato___Healthy",
        "Potato___Late_blight",
        "Corn_(maize)___Common_rust_",
        "Apple___Apple_scab",
    ]
    for cls in test_classes:
        advice = get_advice(cls, "en")
        assert advice["advice"], f"Empty advice for {cls}"
        advice_ur = get_advice(cls, "ur")
        assert advice_ur["advice"], f"Empty Urdu advice for {cls}"
        advice_sd = get_advice(cls, "sd")
        assert advice_sd["advice"], f"Empty Sindhi advice for {cls}"
        print(f"  Advice OK for {cls}")


if __name__ == "__main__":
    pytest.main([__file__, "-v"])
