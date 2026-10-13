# AgroVision Sindh — Flask Backend API
# Reuses existing model, class labels, and preprocessing from Plant-Disease-Detector

import os
import io
import numpy as np
from flask import Flask, request, jsonify
from flask_cors import CORS
from PIL import Image

# ─── Suppress TensorFlow OneDNN warnings ───────────────────────────────────
os.environ["TF_ENABLE_ONEDNN_OPTS"] = "0"
os.environ["TF_CPP_MIN_LOG_LEVEL"] = "2"

import tensorflow as tf

from disease_advice import DISEASE_ADVICE, get_advice

# ─── Configuration ──────────────────────────────────────────────────────────
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# Path to the existing trained model (copied from Deploy/ folder)
MODEL_PATH = os.path.join(BASE_DIR, "model_4_mobilenet_finetuned.keras")

# Confidence threshold: same value used in original Deploy/app.py
CONFIDENCE_THRESHOLD = 70.0

# ─── Class labels — EXACTLY as in Deploy/app.py ────────────────────────────
# (38 PlantVillage classes, matching model output indices)
CLASS_NAMES = [
    'Apple___Apple_scab', 'Apple___Black_rot', 'Apple___Cedar_apple_rust', 'Apple___Healthy',
    'Blueberry___Healthy', 'Cherry_(including_sour)___Powdery_mildew',
    'Cherry_(including_sour)___Healthy',
    'Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot', 'Corn_(maize)___Common_rust_',
    'Corn_(maize)___Northern_Leaf_Blight', 'Corn_(maize)___Healthy', 'Grape___Black_rot',
    'Grape___Esca_(Black_Measles)', 'Grape___Leaf_blight_(Isariopsis_Leaf_Spot)',
    'Grape___Healthy', 'Orange___Haunglongbing_(Citrus_greening)', 'Peach___Bacterial_spot',
    'Peach___Healthy', 'Pepper,_bell___Bacterial_spot', 'Pepper,_bell___Healthy',
    'Potato___Early_blight', 'Potato___Late_blight', 'Potato___Healthy',
    'Raspberry___Healthy', 'Soybean___Healthy', 'Squash___Powdery_mildew',
    'Strawberry___Leaf_scorch', 'Strawberry___Healthy', 'Tomato___Bacterial_spot',
    'Tomato___Early_blight', 'Tomato___Late_blight', 'Tomato___Leaf_Mold',
    'Tomato___Septoria_leaf_spot', 'Tomato___Spider_mites Two-spotted_spider_mite',
    'Tomato___Target_Spot', 'Tomato___Tomato_Yellow_Leaf_Curl_Virus',
    'Tomato___Tomato_mosaic_virus', 'Tomato___Healthy'
]

# ─── Load model once at startup ─────────────────────────────────────────────
print("[AgroVision] Loading model from:", MODEL_PATH)
if not os.path.exists(MODEL_PATH):
    raise FileNotFoundError(
        f"Model file not found at {MODEL_PATH}. "
        "Please copy model_4_mobilenet_finetuned.keras into the backend/ folder."
    )

model = tf.keras.models.load_model(MODEL_PATH, compile=False, safe_mode=False)
print("[AgroVision] Model loaded successfully.")
print(f"[AgroVision] Model input shape: {model.input_shape}")

# ─── Flask app ───────────────────────────────────────────────────────────────
app = Flask(__name__)
CORS(app)  # Allow requests from Android emulator / device


def preprocess_image(image_bytes: bytes) -> np.ndarray:
    """
    Preprocess an image for MobileNetV2 inference.
    Replicates the logic in Deploy/app.py:
      - Resize to 224×224
      - Convert to RGB
      - img_to_array
      - expand_dims to add batch dimension
    NOTE: The model has preprocessing layers baked in (rescales to [-1,1]),
    so we do NOT rescale manually here — matching Deploy/app.py exactly.
    """
    img = Image.open(io.BytesIO(image_bytes)).convert("RGB")
    img = img.resize((224, 224))
    img_array = tf.keras.preprocessing.image.img_to_array(img)  # shape (224,224,3)
    img_array = tf.expand_dims(img_array, 0)  # shape (1,224,224,3)
    return img_array


@app.route("/health", methods=["GET"])
def health():
    """Health check endpoint."""
    return jsonify({"status": "ok", "model": "model_4_mobilenet_finetuned", "classes": len(CLASS_NAMES)})


@app.route("/predict", methods=["POST"])
def predict():
    """
    Main prediction endpoint.
    Accepts: multipart/form-data with field 'image' (jpg/jpeg/png)
    Returns JSON:
    {
      "status": "ok" | "low_confidence" | "error",
      "class_name": str,
      "crop_en": str, "crop_ur": str, "crop_sd": str,
      "disease_en": str, "disease_ur": str, "disease_sd": str,
      "confidence": float,
      "advice_en": str, "advice_ur": str, "advice_sd": str,
      "warning": str | null,
      "disclaimer": str
    }
    """
    if "image" not in request.files:
        return jsonify({"status": "error", "message": "No image field in request."}), 400

    file = request.files["image"]
    if file.filename == "":
        return jsonify({"status": "error", "message": "Empty filename."}), 400

    try:
        image_bytes = file.read()
        img_array = preprocess_image(image_bytes)

        # Run inference — identical to Deploy/app.py
        predictions = model.predict(img_array, verbose=0)
        score = predictions[0]

        confidence = float(100.0 * np.max(score))
        class_idx = int(np.argmax(score))
        predicted_class = CLASS_NAMES[class_idx]

        # Build multi-language advice
        advice_en = get_advice(predicted_class, "en")
        advice_ur = get_advice(predicted_class, "ur")
        advice_sd = get_advice(predicted_class, "sd")

        # Top-3 predictions for debugging / transparency
        top3_indices = np.argsort(score)[::-1][:3]
        top3 = [
            {"class": CLASS_NAMES[i], "confidence": float(100.0 * score[i])}
            for i in top3_indices
        ]

        disclaimer = (
            "⚠️ This is an AI-assisted screening tool, not a confirmed diagnosis. "
            "Consult a qualified agricultural expert before taking crop management action."
        )

        if confidence < CONFIDENCE_THRESHOLD:
            return jsonify({
                "status": "low_confidence",
                "class_name": predicted_class,
                "crop_en": advice_en["crop"],
                "crop_ur": advice_ur["crop"],
                "crop_sd": advice_sd["crop"],
                "disease_en": advice_en["display"],
                "disease_ur": advice_ur["display"],
                "disease_sd": advice_sd["display"],
                "confidence": round(confidence, 2),
                "advice_en": advice_en["advice"],
                "advice_ur": advice_ur["advice"],
                "advice_sd": advice_sd["advice"],
                "warning": (
                    f"The model is only {confidence:.1f}% confident. "
                    "This image may not show a supported crop leaf, or the image quality is too low. "
                    "Please upload a clear, close-up photo of a crop leaf."
                ),
                "top3": top3,
                "disclaimer": disclaimer,
            })
        else:
            return jsonify({
                "status": "ok",
                "class_name": predicted_class,
                "crop_en": advice_en["crop"],
                "crop_ur": advice_ur["crop"],
                "crop_sd": advice_sd["crop"],
                "disease_en": advice_en["display"],
                "disease_ur": advice_ur["display"],
                "disease_sd": advice_sd["display"],
                "confidence": round(confidence, 2),
                "advice_en": advice_en["advice"],
                "advice_ur": advice_ur["advice"],
                "advice_sd": advice_sd["advice"],
                "warning": None,
                "top3": top3,
                "disclaimer": disclaimer,
            })

    except Exception as e:
        return jsonify({
            "status": "error",
            "message": f"Prediction failed: {str(e)}",
        }), 500


@app.route("/classes", methods=["GET"])
def list_classes():
    """Returns all supported class names."""
    return jsonify({"classes": CLASS_NAMES, "count": len(CLASS_NAMES)})


if __name__ == "__main__":
    print("[AgroVision] Starting Flask API on port 5000 ...")
    print("[AgroVision] Connect your Android app to: http://10.0.2.2:5000 (emulator)")
    print("[AgroVision]                           or: http://<YOUR_PC_IP>:5000 (device)")
    app.run(host="0.0.0.0", port=5000, debug=False)
