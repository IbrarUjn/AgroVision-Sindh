package com.agrovision.sindh;

import com.google.gson.annotations.SerializedName;

/**
 * Response received from POST /chat endpoint.
 */
public class ChatResponse {

    @SerializedName("status")
    public String status; // "ok" or "error"

    @SerializedName("reply")
    public String reply;

    @SerializedName("source")
    public String source; // "gemini_api" or "local_guidance_fallback"

    @SerializedName("model")
    public String model;

    @SerializedName("api_key_configured")
    public boolean apiKeyConfigured;

    @SerializedName("disclaimer")
    public String disclaimer;

    @SerializedName("message")
    public String message; // error message if status == "error"
}
