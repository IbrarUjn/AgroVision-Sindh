package com.agrovision.sindh.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Clean wrapper for Android SpeechRecognizer.
 * Coordinates speech recognition sessions, partial/final transcription,
 * state changes, and lifecycle cleanup.
 */
public class AgroVisionSpeechManager {

    private static final String TAG = "AgroVisionSTT";

    public interface SpeechStateListener {
        void onListeningStarted();
        void onListeningFinished();
        void onRmsChanged(float rmsdB);
        void onPartialTranscription(String partialText);
        void onFinalTranscription(String finalText);
        void onSpeechError(String errorMessage);
    }

    private final Context context;
    private SpeechRecognizer speechRecognizer;
    private boolean isListening = false;
    private SpeechStateListener listener;
    private String currentLanguage = "en";

    public AgroVisionSpeechManager(Context context, SpeechStateListener listener) {
        this.context = context.getApplicationContext();
        this.listener = listener;
    }

    public static boolean isRecognitionAvailable(Context context) {
        return SpeechRecognizer.isRecognitionAvailable(context);
    }

    public boolean isListening() {
        return isListening;
    }

    /**
     * Starts listening with language preference (en, ur, sd).
     */
    public synchronized boolean startListening(String langCode) {
        if (isListening) {
            Log.w(TAG, "Already listening, ignoring redundant start request");
            return false;
        }

        if (!isRecognitionAvailable(context)) {
            if (listener != null) {
                listener.onSpeechError("Speech recognition is not available on this device.");
            }
            return false;
        }

        this.currentLanguage = langCode;

        // Ensure clean recognizer instance
        destroyRecognizer();

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    isListening = true;
                    if (listener != null) {
                        listener.onListeningStarted();
                    }
                }

                @Override
                public void onBeginningOfSpeech() {
                    // Farmer started speaking
                }

                @Override
                public void onRmsChanged(float rmsdB) {
                    if (listener != null) {
                        listener.onRmsChanged(rmsdB);
                    }
                }

                @Override
                public void onBufferReceived(byte[] buffer) {}

                @Override
                public void onEndOfSpeech() {
                    isListening = false;
                    if (listener != null) {
                        listener.onListeningFinished();
                    }
                }

                @Override
                public void onError(int error) {
                    isListening = false;
                    String message = getErrorMessage(error);
                    Log.e(TAG, "SpeechRecognizer error " + error + ": " + message);
                    if (listener != null) {
                        listener.onListeningFinished();
                        listener.onSpeechError(message);
                    }
                }

                @Override
                public void onResults(Bundle results) {
                    isListening = false;
                    if (listener != null) {
                        listener.onListeningFinished();
                    }
                    if (results != null) {
                        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        if (matches != null && !matches.isEmpty()) {
                            String bestMatch = matches.get(0).trim();
                            if (!bestMatch.isEmpty() && listener != null) {
                                listener.onFinalTranscription(bestMatch);
                            }
                        }
                    }
                }

                @Override
                public void onPartialResults(Bundle partialResults) {
                    if (partialResults != null) {
                        ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        if (matches != null && !matches.isEmpty()) {
                            String partial = matches.get(0).trim();
                            if (!partial.isEmpty() && listener != null) {
                                listener.onPartialTranscription(partial);
                            }
                        }
                    }
                }

                @Override
                public void onEvent(int eventType, Bundle params) {}
            });

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

            // Set primary and fallback language tags
            String primaryTag;
            String[] additionalTags;

            if ("ur".equalsIgnoreCase(langCode)) {
                primaryTag = "ur-PK";
                additionalTags = new String[]{"ur-PK", "ur", "ur-IN"};
            } else if ("sd".equalsIgnoreCase(langCode)) {
                // Sindhi in Pakistan uses Arabic script (sd-Arab-PK or sd-PK)
                // Also provide Urdu as secondary regional fallback so recognizer doesn't fail silently
                primaryTag = "sd-PK";
                additionalTags = new String[]{"sd-PK", "sd-Arab-PK", "sd", "ur-PK"};
            } else {
                primaryTag = "en-US";
                additionalTags = new String[]{"en-US", "en-GB", "en"};
            }

            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, primaryTag);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, primaryTag);
            intent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", additionalTags);
            intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now / بولیں / ڳالهايو");

            speechRecognizer.startListening(intent);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error starting speech recognition", e);
            isListening = false;
            if (listener != null) {
                listener.onSpeechError("Could not start speech recognition: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Cancels the active speech recognition session.
     */
    public synchronized void cancel() {
        if (speechRecognizer != null) {
            try {
                speechRecognizer.cancel();
            } catch (Exception ignored) {}
        }
        isListening = false;
        if (listener != null) {
            listener.onListeningFinished();
        }
    }

    /**
     * Stops listening and processes audio recorded so far.
     */
    public synchronized void stopListening() {
        if (speechRecognizer != null) {
            try {
                speechRecognizer.stopListening();
            } catch (Exception ignored) {}
        }
        isListening = false;
    }

    /**
     * Releases all recognizer resources. Must be called in Activity onDestroy.
     */
    public synchronized void destroy() {
        listener = null;
        destroyRecognizer();
    }

    private void destroyRecognizer() {
        if (speechRecognizer != null) {
            try {
                speechRecognizer.cancel();
                speechRecognizer.destroy();
            } catch (Exception ignored) {}
            speechRecognizer = null;
        }
        isListening = false;
    }

    private String getErrorMessage(int errorCode) {
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                return "Audio recording error.";
            case SpeechRecognizer.ERROR_CLIENT:
                return "Client error. Please try again.";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                return "Insufficient microphone permission.";
            case SpeechRecognizer.ERROR_NETWORK:
                return "Network connection error.";
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                return "Network timeout. Check your internet connection.";
            case SpeechRecognizer.ERROR_NO_MATCH:
                return "No speech was recognized. Please speak clearly.";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                return "Recognition service is busy. Please wait a moment.";
            case SpeechRecognizer.ERROR_SERVER:
                return "Recognition server error. Try again.";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                return "No speech detected within the time limit.";
            default:
                return "Voice recognition error (" + errorCode + ")";
        }
    }
}
