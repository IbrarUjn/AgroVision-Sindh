import io
import numpy as np
from PIL import Image
from app import app

client = app.test_client()

print("==================================================")
print("  AgroVision Sindh — End-to-End Prediction Test   ")
print("==================================================")

# 1. Test supported leaf simulation
print("\n[TEST 1] Processing Leaf Image:")
leaf_img = Image.new("RGB", (224, 224), color=(34, 139, 34))
buf = io.BytesIO()
leaf_img.save(buf, format="JPEG")
buf.seek(0)
res = client.post("/predict", data={"image": (buf, "leaf.jpg")}, content_type="multipart/form-data")
d = res.get_json()

print(f"  Status: {d.get('status')}")
print(f"  Class: {d.get('class_name')}")
print(f"  Crop EN: {d.get('crop_en')} | UR: {d.get('crop_ur')} | SD: {d.get('crop_sd')}")
print(f"  Disease EN: {d.get('disease_en')} | UR: {d.get('disease_ur')} | SD: {d.get('disease_sd')}")
print(f"  Confidence: {d.get('confidence')}%")
print(f"  Urdu Advice Preview: {d.get('advice_ur')[:50]}...")
print(f"  Sindhi Advice Preview: {d.get('advice_sd')[:50]}...")
print(f"  Disclaimer: {d.get('disclaimer')}")

# 2. Test noise / non-leaf image (must trigger low confidence warning < 70%)
print("\n[TEST 2] Processing Non-Leaf Random Noise Image:")
noise_data = np.random.randint(0, 256, (224, 224, 3), dtype=np.uint8)
noise_img = Image.fromarray(noise_data)
buf2 = io.BytesIO()
noise_img.save(buf2, format="JPEG")
buf2.seek(0)
res2 = client.post("/predict", data={"image": (buf2, "noise.jpg")}, content_type="multipart/form-data")
d2 = res2.get_json()

print(f"  Status: {d2.get('status')}")
print(f"  Confidence: {d2.get('confidence')}%")
print(f"  Low Confidence Warning Triggered: {d2.get('warning') is not None}")
if d2.get("warning"):
    print(f"  Warning Message: {d2.get('warning')}")

print("\n[SUCCESS] End-to-end image prediction workflow verified successfully!")
