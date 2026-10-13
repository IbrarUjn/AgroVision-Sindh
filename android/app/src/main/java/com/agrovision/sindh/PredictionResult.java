package com.agrovision.sindh;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * POJO for the JSON response from POST /predict.
 * Maps to the exact keys returned by the Flask backend.
 */
public class PredictionResult {

    @SerializedName("status")
    public String status; // "ok", "low_confidence", "error"

    @SerializedName("class_name")
    public String className;

    @SerializedName("crop_en")
    public String cropEn;

    @SerializedName("crop_ur")
    public String cropUr;

    @SerializedName("crop_sd")
    public String cropSd;

    @SerializedName("disease_en")
    public String diseaseEn;

    @SerializedName("disease_ur")
    public String diseaseUr;

    @SerializedName("disease_sd")
    public String diseaseSd;

    @SerializedName("confidence")
    public float confidence;

    @SerializedName("advice_en")
    public String adviceEn;

    @SerializedName("advice_ur")
    public String adviceUr;

    @SerializedName("advice_sd")
    public String adviceSd;

    @SerializedName("warning")
    public String warning; // null if confidence >= 70%

    @SerializedName("disclaimer")
    public String disclaimer;

    @SerializedName("top3")
    public List<Top3Item> top3;

    @SerializedName("message")
    public String message; // For error responses

    /** Helper: returns true if result is a high-confidence detection */
    public boolean isHighConfidence() {
        return "ok".equals(status);
    }

    /** Helper: returns true if the prediction is uncertain */
    public boolean isLowConfidence() {
        return "low_confidence".equals(status);
    }

    /** Returns localized crop name based on language code */
    public String getCrop(String lang) {
        switch (lang) {
            case "ur": return cropUr != null ? cropUr : cropEn;
            case "sd": return cropSd != null ? cropSd : cropEn;
            default:   return cropEn;
        }
    }

    /** Returns localized disease name based on language code */
    public String getDisease(String lang) {
        switch (lang) {
            case "ur": return diseaseUr != null ? diseaseUr : diseaseEn;
            case "sd": return diseaseSd != null ? diseaseSd : diseaseEn;
            default:   return diseaseEn;
        }
    }

    /** Returns localized advice based on language code */
    public String getAdvice(String lang) {
        switch (lang) {
            case "ur": return adviceUr != null ? adviceUr : adviceEn;
            case "sd": return adviceSd != null ? adviceSd : adviceEn;
            default:   return adviceEn;
        }
    }

    /** Whether the detected plant is healthy (no disease) */
    public boolean isHealthy() {
        return className != null && className.toLowerCase().contains("healthy");
    }

    public static class Top3Item {
        @SerializedName("class")
        public String className;

        @SerializedName("confidence")
        public float confidence;
    }
}
