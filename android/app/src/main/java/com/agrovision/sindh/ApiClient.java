package com.agrovision.sindh;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Handles HTTP communication with the AgroVision Sindh Flask API.
 * Sends an image URI as multipart/form-data and returns raw JSON.
 */
public class ApiClient {

    // Candidate base URLs to try in order:
    // 1. 127.0.0.1:5000 (Physical device via USB `adb reverse tcp:5000 tcp:5000`)
    // 2. 192.168.1.15:5000 (Physical device via local Wi-Fi network)
    // 3. 10.0.2.2:5000 (Android Studio Emulator)
    private static final String[] CANDIDATE_BASE_URLS = new String[] {
            "http://127.0.0.1:5000",
            "http://192.168.1.15:5000",
            "http://10.0.2.2:5000"
    };

    private static final int CONNECT_TIMEOUT_SECONDS = 5;
    private static final int READ_TIMEOUT_SECONDS = 30;

    private final OkHttpClient httpClient;

    public ApiClient() {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
    }

    public interface PredictionCallback {
        void onSuccess(String jsonResponse);
        void onFailure(String errorMessage);
    }

    /**
     * Converts a content URI to a temp File so OkHttp can read it.
     */
    private File uriToFile(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        if (inputStream == null) throw new IOException("Cannot open URI: " + uri);

        File tempFile = File.createTempFile("agrovision_upload_", ".jpg", context.getCacheDir());
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } finally {
            inputStream.close();
        }
        return tempFile;
    }

    /**
     * Posts an image to POST /predict and returns raw JSON via callback.
     * Callback is called on a background thread — post to UI thread as needed.
     */
    public void predict(Context context, Uri imageUri, PredictionCallback callback) {
        try {
            File imageFile = uriToFile(context, imageUri);
            tryCandidate(imageFile, 0, callback, null);
        } catch (IOException e) {
            callback.onFailure("Image processing error: " + e.getMessage());
        }
    }

    private void tryCandidate(File imageFile, int index, PredictionCallback callback, String lastError) {
        if (index >= CANDIDATE_BASE_URLS.length) {
            callback.onFailure("Cannot reach Flask backend after trying:\n" +
                    "• 127.0.0.1 (USB adb reverse)\n" +
                    "• 192.168.1.15 (Wi-Fi)\n" +
                    "• 10.0.2.2 (Emulator)\n\n" +
                    "Last error: " + lastError + "\n\n" +
                    "Please make sure the Flask API is running on your PC and run:\n" +
                    "adb reverse tcp:5000 tcp:5000");
            return;
        }

        String baseUrl = CANDIDATE_BASE_URLS[index];
        String predictUrl = baseUrl + "/predict";

        RequestBody fileBody = RequestBody.create(
                imageFile, MediaType.parse("image/jpeg"));

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", imageFile.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(predictUrl)
                .post(requestBody)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Call call,
                                  @androidx.annotation.NonNull IOException e) {
                tryCandidate(imageFile, index + 1, callback, e.getMessage());
            }

            @Override
            public void onResponse(@androidx.annotation.NonNull Call call,
                                   @androidx.annotation.NonNull Response response)
                    throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    callback.onSuccess(json);
                } else {
                    tryCandidate(imageFile, index + 1, callback, "HTTP " + response.code());
                }
            }
        });
    }

    public interface ChatCallback {
        void onSuccess(ChatResponse chatResponse);
        void onFailure(String errorMessage);
    }

    /**
     * Sends a chat prompt to the AI Farmer Assistant backend endpoint (POST /chat)
     * with multi-endpoint fallback across USB adb reverse, Wi-Fi, and Emulator.
     */
    public void sendChat(ChatRequest chatRequest, ChatCallback callback) {
        com.google.gson.Gson gson = new com.google.gson.Gson();
        String jsonPayload = gson.toJson(chatRequest);
        tryChatCandidate(jsonPayload, 0, callback, null);
    }

    private void tryChatCandidate(String jsonPayload, int index, ChatCallback callback, String lastError) {
        if (index >= CANDIDATE_BASE_URLS.length) {
            callback.onFailure("Cannot reach AI Assistant backend after trying endpoints.\n" +
                    "Last error: " + lastError + "\n\n" +
                    "Please ensure the Flask backend is running on your PC and run:\n" +
                    "adb reverse tcp:5000 tcp:5000");
            return;
        }

        String baseUrl = CANDIDATE_BASE_URLS[index];
        String chatUrl = baseUrl + "/chat";

        RequestBody body = RequestBody.create(
                jsonPayload, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(chatUrl)
                .post(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Call call,
                                  @androidx.annotation.NonNull IOException e) {
                tryChatCandidate(jsonPayload, index + 1, callback, e.getMessage());
            }

            @Override
            public void onResponse(@androidx.annotation.NonNull Call call,
                                   @androidx.annotation.NonNull Response response)
                    throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String respString = response.body().string();
                        com.google.gson.Gson gson = new com.google.gson.Gson();
                        ChatResponse chatResponse = gson.fromJson(respString, ChatResponse.class);
                        if (chatResponse != null && "ok".equals(chatResponse.status)) {
                            callback.onSuccess(chatResponse);
                        } else {
                            String err = (chatResponse != null && chatResponse.message != null)
                                    ? chatResponse.message : "Server error";
                            callback.onFailure(err);
                        }
                    } catch (Exception ex) {
                        tryChatCandidate(jsonPayload, index + 1, callback, "Parse error: " + ex.getMessage());
                    }
                } else {
                    tryChatCandidate(jsonPayload, index + 1, callback, "HTTP " + response.code());
                }
            }
        });
    }

    public interface AudioCallback {
        void onSuccess(byte[] audioBytes);
        void onFailure(String errorMessage);
    }

    /**
     * Fetches synthesized speech MP3 bytes from backend /tts endpoint for Urdu/Sindhi voice.
     */
    public void fetchTtsAudio(String text, String language, AudioCallback callback) {
        org.json.JSONObject payload = new org.json.JSONObject();
        try {
            payload.put("text", text);
            payload.put("language", language);
        } catch (org.json.JSONException ignored) {}

        tryTtsCandidate(payload.toString(), 0, callback, null);
    }

    private void tryTtsCandidate(String jsonPayload, int index, AudioCallback callback, String lastError) {
        if (index >= CANDIDATE_BASE_URLS.length) {
            callback.onFailure("Audio synthesis unreachable: " + (lastError != null ? lastError : "all endpoints failed"));
            return;
        }

        String targetUrl = CANDIDATE_BASE_URLS[index] + "/tts";
        MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonPayload, JSON_TYPE);
        Request request = new Request.Builder().url(targetUrl).post(body).build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Call call, @androidx.annotation.NonNull IOException e) {
                tryTtsCandidate(jsonPayload, index + 1, callback, e.getMessage());
            }

            @Override
            public void onResponse(@androidx.annotation.NonNull Call call, @androidx.annotation.NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    byte[] bytes = response.body().bytes();
                    callback.onSuccess(bytes);
                } else {
                    tryTtsCandidate(jsonPayload, index + 1, callback, "HTTP " + response.code());
                }
            }
        });
    }

    /**
     * Quick health check — returns true if the API responds.
     */
    public void checkHealth(PredictionCallback callback) {
        Request request = new Request.Builder().url(CANDIDATE_BASE_URLS[0] + "/health").get().build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Call call,
                                  @androidx.annotation.NonNull IOException e) {
                callback.onFailure("API unreachable: " + e.getMessage());
            }

            @Override
            public void onResponse(@androidx.annotation.NonNull Call call,
                                   @androidx.annotation.NonNull Response response)
                    throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().string());
                } else {
                    callback.onFailure("API health check failed: HTTP " + response.code());
                }
            }
        });
    }
}
