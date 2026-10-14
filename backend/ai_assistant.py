# ai_assistant.py
# AgroVision AI Farmer Assistant Service
# Securely interfaces with Gemini API or transparently falls back to local verified guidance.

import os
import json
import urllib.request
import urllib.error
from typing import List, Dict, Optional, Any

from disease_advice import DISEASE_ADVICE, get_advice

# Gemini API configuration
# Read securely from environment variable GEMINI_API_KEY (never hardcoded)
GEMINI_API_KEY = os.environ.get("GEMINI_API_KEY", "").strip()
GEMINI_MODEL = os.environ.get("GEMINI_MODEL", "gemini-3.8-flash").strip()
GEMINI_ENDPOINT = f"https://generativelanguage.googleapis.com/v1beta/models/{GEMINI_MODEL}:generateContent"

SYSTEM_INSTRUCTION = """
You are the AgroVision AI Farmer Assistant, an expert agricultural advisor assisting farmers in Sindh, Pakistan.
Your goals:
1. Provide practical, farmer-friendly, and responsible agronomy guidance.
2. If prediction context is provided from the leaf scan model, explain that exact crop and disease prediction. NEVER change the predicted disease class or contradict the ML screening result.
3. Clearly state that AI screening is an assistive tool, not a certified lab diagnosis. Always suggest consulting a local Agriculture Extension Officer (Shaheed Benazirabad / Hyderabad / Sindh Agriculture Department) before applying expensive or toxic chemicals.
4. Avoid prescribing extreme, hazardous, or unverified chemical concoctions. Recommend integrated pest management (IPM), proper sanitation, crop rotation, and balanced irrigation/fertilizer first.
5. Answer in the farmer's chosen language:
   - If language is 'ur', respond entirely in polite, simple Urdu.
   - If language is 'sd', respond entirely in authentic, accessible Sindhi.
   - If language is 'en', respond in clear English.
6. Keep answers concise, actionable, and formatted with bullet points for readability on mobile screens.
"""

def generate_local_fallback_response(message: str, language: str, prediction_context: Optional[Dict[str, Any]]) -> str:
    """
    Transparent local fallback rule-based response when Gemini API key is missing or network fails.
    Uses verified agricultural guidance from disease_advice.py.
    """
    msg_lower = message.lower()
    crop = ""
    disease = ""
    advice = ""
    conf = ""

    if prediction_context:
        crop = prediction_context.get(f"crop_{language}", prediction_context.get("crop_en", ""))
        disease = prediction_context.get(f"disease_{language}", prediction_context.get("disease_en", ""))
        advice = prediction_context.get(f"advice_{language}", prediction_context.get("advice_en", ""))
        conf = str(prediction_context.get("confidence", ""))

    if language == "ur":
        if prediction_context and disease:
            return (
                f"🌱 **تشخیص کی وضاحت:**\n"
                f"ماڈل نے **{crop}** میں **{disease}** کی نشاندہی کی ہے (اعتماد: {conf}%)۔\n\n"
                f"🛡️ **تجویز کردہ حفاظتی تدابیر:**\n"
                f"{advice}\n\n"
                f"⚠️ *نوٹ: یہ خودکار اسکریننگ ہے، تصدیق کے لیے قریبی محکمہ زراعت یا زرعی ماہر سے رابطہ فرمائیں۔*"
            )
        else:
            return (
                "🌱 **ایگرو ویژن کسان معاون (آف لائن موڈ):**\n"
                "آپ اپنی فصل کے پتے کی تصویر اسکین کریں تاکہ ماڈل بیماری کی شناخت کر سکے۔\n"
                "عام رہنمائی: پودوں کو مناسب پانی دیں، بیماری زدہ پتے فوراً الگ کریں اور متوازن کھاد کا استعمال کریں۔"
            )
    elif language == "sd":
        if prediction_context and disease:
            return (
                f"🌱 **تشخيص جي وضاحت:**\n"
                f"ماڊل **{crop}** ۾ **{disease}** سڃاتو آهي (اعتماد: {conf}%)۔\n\n"
                f"🛡️ **احتياطي رهنما تدبيرون:**\n"
                f"{advice}\n\n"
                f"⚠️ *نوٽ: اها هڪ AI مددگار اسڪريننگ آهي. مڪمل تصديق لاءِ سنڌ زراعت کاتي جي مقامي آفيسر سان رابطو ڪريو.*"
            )
        else:
            return (
                "🌱 **ايگرو ويژن هاري مددگار (آف لائن موڊ):**\n"
                "فصل جي بيماري چڪاسڻ لاءِ پنهنجي ٻوٽي جي پن جو فوٽو ڪڍو.\n"
                "عام صلاح: ٻوٽن کي پاڙ ۾ پاڻي ڏيو، متاثر پنن کي جدا ڪريو ۽ متوازن کاد استعمال ڪريو."
            )
    else:  # en
        if prediction_context and disease:
            return (
                f"🌱 **Prediction Summary:**\n"
                f"The AI model identified **{disease}** on **{crop}** with {conf}% confidence.\n\n"
                f"🛡️ **Verified Preventive Actions:**\n"
                f"{advice}\n\n"
                f"⚠️ *Note: This is an AI screening tool. For severe infestations, consult your local agricultural extension service before chemical intervention.*"
            )
        else:
            return (
                "🌱 **AgroVision Farmer Assistant (Standard Guidance):**\n"
                "Please scan or upload a clear crop leaf photo to get a specific diagnosis and tailored prevention plan.\n"
                "General Best Practices:\n"
                "• Avoid overhead watering to reduce fungal leaf wetness.\n"
                "• Prune and safely dispose of infected foliage.\n"
                "• Ensure proper spacing for adequate sunlight and aeration."
            )


def ask_ai_assistant(
    message: str,
    language: str = "en",
    prediction_context: Optional[Dict[str, Any]] = None,
    history: Optional[List[Dict[str, str]]] = None,
) -> Dict[str, Any]:
    """
    Main entry point for AI chat.
    If GEMINI_API_KEY is available, queries Gemini API.
    Otherwise, falls back to structured agronomy guidance.
    """
    api_key = os.environ.get("GEMINI_API_KEY", "").strip() or GEMINI_API_KEY

    # Build prompt context
    context_str = ""
    if prediction_context:
        c_name = prediction_context.get("class_name", "")
        crop_en = prediction_context.get("crop_en", "")
        dis_en = prediction_context.get("disease_en", "")
        conf = prediction_context.get("confidence", "")
        advice_en = prediction_context.get("advice_en", "")
        advice_ur = prediction_context.get("advice_ur", "")
        advice_sd = prediction_context.get("advice_sd", "")

        context_str = (
            f"\n[ACTIVE CROP LEAF PREDICTION CONTEXT]\n"
            f"- Crop: {crop_en}\n"
            f"- Predicted Condition: {dis_en} (Model class: {c_name})\n"
            f"- Confidence Score: {conf}%\n"
            f"- Verified Prevention (EN): {advice_en}\n"
            f"- Verified Prevention (UR): {advice_ur}\n"
            f"- Verified Prevention (SD): {advice_sd}\n"
            f"Ground your answer in this prediction. Do NOT invent a different disease diagnosis.\n"
        )

    if not api_key:
        fallback_text = generate_local_fallback_response(message, language, prediction_context)
        return {
            "reply": fallback_text,
            "source": "local_guidance_fallback",
            "model": "rule_based_verified_advice",
            "api_key_configured": False,
            "disclaimer": (
                "⚠️ Powered by AgroVision local agricultural knowledge base. "
                "Configure GEMINI_API_KEY in the backend environment for cloud generative reasoning."
            ),
        }

    # Prepare Gemini payload with conversation history
    contents = []
    
    # 1. System instruction via first message or system context
    intro = f"{SYSTEM_INSTRUCTION}\nPreferred Response Language: {language}.\n{context_str}"
    contents.append({
        "role": "user",
        "parts": [{"text": intro}]
    })
    contents.append({
        "role": "model",
        "parts": [{"text": f"Understood. I am AgroVision AI Farmer Assistant. I will communicate in language '{language}' adhering to safe agricultural practices."}]
    })

    # 2. Append recent history (bounded up to last 6 messages)
    if history:
        for item in history[-6:]:
            role = "user" if item.get("role") == "user" else "model"
            text = item.get("text", "").strip()
            if text:
                contents.append({
                    "role": role,
                    "parts": [{"text": text}]
                })

    # 3. Append current user message
    contents.append({
        "role": "user",
        "parts": [{"text": message}]
    })

    # Candidate models to try in order of availability and speed
    candidate_models = [
        os.environ.get("GEMINI_MODEL", "gemini-flash-latest").strip(),
        "gemini-3.8-flash",
        "gemini-3.5-flash",
        "gemini-3.1-flash-lite"
    ]
    # Remove duplicates while preserving order
    models_to_try = []
    for m in candidate_models:
        if m and m not in models_to_try:
            models_to_try.append(m)

    headers = {"Content-Type": "application/json"}
    body = {
        "contents": contents,
        "generationConfig": {
            "temperature": 0.3,
            "maxOutputTokens": 600,
        }
    }
    encoded_body = json.dumps(body).encode("utf-8")

    last_error_detail = None
    for target_model in models_to_try:
        url = f"https://generativelanguage.googleapis.com/v1beta/models/{target_model}:generateContent?key={api_key}"
        try:
            req = urllib.request.Request(
                url,
                data=encoded_body,
                headers=headers,
                method="POST",
            )
            with urllib.request.urlopen(req, timeout=12) as resp:
                data = json.loads(resp.read().decode("utf-8"))
                candidates = data.get("candidates", [])
                if candidates and "content" in candidates[0]:
                    parts = candidates[0]["content"].get("parts", [])
                    if parts and "text" in parts[0]:
                        reply_text = parts[0]["text"]
                        return {
                            "reply": reply_text,
                            "source": "gemini_api",
                            "model": target_model,
                            "api_key_configured": True,
                            "disclaimer": "⚠️ AI response generated by Gemini. Always consult local agricultural authorities for verified treatment."
                        }
        except urllib.error.HTTPError as e:
            last_error_detail = f"Gemini API HTTP Error {e.code}: {e.reason} ({target_model})"
            continue
        except Exception as e:
            last_error_detail = f"{str(e)} ({target_model})"
            continue

    # If all cloud models failed or experienced temporary upstream load, fall back gracefully
    fallback_text = generate_local_fallback_response(message, language, prediction_context)
    return {
        "reply": fallback_text,
        "source": "local_guidance_fallback",
        "model": "rule_based_verified_advice",
        "api_key_configured": True,
        "error_detail": last_error_detail,
        "disclaimer": "⚠️ Falling back to local verified agricultural guidance due to cloud API limit or error."
    }

