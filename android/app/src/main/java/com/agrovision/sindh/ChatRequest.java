package com.agrovision.sindh;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

/**
 * Payload sent to POST /chat endpoint on the backend.
 */
public class ChatRequest {

    @SerializedName("message")
    public String message;

    @SerializedName("language")
    public String language;

    @SerializedName("prediction_context")
    public Map<String, Object> predictionContext;

    @SerializedName("history")
    public List<HistoryItem> history;

    public static class HistoryItem {
        @SerializedName("role")
        public String role; // "user" or "model"

        @SerializedName("text")
        public String text;

        public HistoryItem(String role, String text) {
            this.role = role;
            this.text = text;
        }
    }
}
