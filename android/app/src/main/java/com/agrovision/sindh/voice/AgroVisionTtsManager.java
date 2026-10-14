package com.agrovision.sindh.voice;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.agrovision.sindh.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Clean wrapper for Android TextToSpeech engine.
 * Safely handles engine initialization, language selection, error handling,
 * and lifecycle teardown.
 */
public class AgroVisionTtsManager implements TextToSpeech.OnInitListener {

    private static final String TAG = "AgroVisionTTS";

    public interface TtsStateListener {
        void onTtsInitialized(boolean success);
        void onSpeechStarted(String messageId);
        void onSpeechCompleted(String messageId);
        void onSpeechError(String messageId, String errorDescription);
    }

    private final Context context;
    private TextToSpeech tts;
    private android.media.MediaPlayer mediaPlayer;
    private final com.agrovision.sindh.ApiClient apiClient;
    private boolean isInitialized = false;
    private String currentlySpeakingMessageId = null;
    private TtsStateListener listener;
    private String currentLanguageCode = "en";

    public AgroVisionTtsManager(Context context, TtsStateListener listener) {
        this.context = context.getApplicationContext();
        this.listener = listener;
        this.apiClient = new com.agrovision.sindh.ApiClient();
        try {
            this.tts = new TextToSpeech(this.context, this);
        } catch (Exception e) {
            Log.e(TAG, "Failed to instantiate TextToSpeech", e);
            if (listener != null) {
                listener.onTtsInitialized(false);
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS && tts != null) {
            isInitialized = true;
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    currentlySpeakingMessageId = utteranceId;
                    if (listener != null) {
                        listener.onSpeechStarted(utteranceId);
                    }
                }

                @Override
                public void onDone(String utteranceId) {
                    if (utteranceId != null && utteranceId.equals(currentlySpeakingMessageId)) {
                        currentlySpeakingMessageId = null;
                    }
                    if (listener != null) {
                        listener.onSpeechCompleted(utteranceId);
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    if (utteranceId != null && utteranceId.equals(currentlySpeakingMessageId)) {
                        currentlySpeakingMessageId = null;
                    }
                    if (listener != null) {
                        listener.onSpeechError(utteranceId, "Playback error");
                    }
                }

                @Override
                public void onError(String utteranceId, int errorCode) {
                    if (utteranceId != null && utteranceId.equals(currentlySpeakingMessageId)) {
                        currentlySpeakingMessageId = null;
                    }
                    if (listener != null) {
                        listener.onSpeechError(utteranceId, "TTS error code " + errorCode);
                    }
                }
            });

            // Set default language
            applyLanguage(currentLanguageCode);

            if (listener != null) {
                listener.onTtsInitialized(true);
            }
        } else {
            isInitialized = false;
            Log.e(TAG, "TextToSpeech init failed with status: " + status);
            if (listener != null) {
                listener.onTtsInitialized(false);
            }
        }
    }

    /**
     * Resolves and applies locale based on app language (en, ur, sd).
     */
    /**
     * Resolves and applies locale based on app language (en, ur, sd).
     * Attempts exact regional tag first, then broad language tag.
     */
    public boolean applyLanguage(String langCode) {
        this.currentLanguageCode = langCode;
        if (!isInitialized || tts == null) return false;

        List<Locale> candidateLocales = new ArrayList<>();
        if ("ur".equalsIgnoreCase(langCode)) {
            candidateLocales.add(new Locale("ur", "PK"));
            candidateLocales.add(new Locale("ur"));
            candidateLocales.add(new Locale("ur", "IN"));
        } else if ("sd".equalsIgnoreCase(langCode)) {
            // Sindhi (Sindh, Pakistan)
            candidateLocales.add(new Locale("sd", "PK"));
            candidateLocales.add(new Locale("sd"));
            // If Sindhi TTS voice packet is not installed on user's device,
            // fall back to Urdu (national language of Pakistan, universally intelligible in Sindh)
            // so the farmer still hears spoken voice rather than complete silence
            candidateLocales.add(new Locale("ur", "PK"));
            candidateLocales.add(new Locale("ur"));
        } else {
            candidateLocales.add(Locale.US);
            candidateLocales.add(Locale.ENGLISH);
        }

        for (Locale loc : candidateLocales) {
            try {
                int res = tts.isLanguageAvailable(loc);
                if (res >= TextToSpeech.LANG_AVAILABLE) {
                    int setResult = tts.setLanguage(loc);
                    if (setResult >= TextToSpeech.LANG_AVAILABLE) {
                        Log.i(TAG, "Successfully loaded voice for locale: " + loc);
                        return true;
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "Error checking locale " + loc, e);
            }
        }

        // Try direct setLanguage on primary locale as best-effort
        try {
            Locale primary = candidateLocales.get(0);
            int res = tts.setLanguage(primary);
            return (res != TextToSpeech.LANG_MISSING_DATA && res != TextToSpeech.LANG_NOT_SUPPORTED);
        } catch (Exception e) {
            Log.e(TAG, "Direct setLanguage failed", e);
            return false;
        }
    }

    /**
     * Speaks the specified text with messageId as the utterance tag.
     * Stops any prior speech before beginning.
    /**
     * Speaks the specified text with messageId as the utterance tag.
     * Stops any prior speech before beginning.
     */
    public boolean speak(String messageId, String text, String langCode) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        stop();

        String speechCleaned = cleanMarkdownForSpeech(text);

        // For Urdu and Sindhi, try cloud audio synthesis via backend first
        // to guarantee high-quality pronunciation without relying on phone voice packets
        if ("ur".equalsIgnoreCase(langCode) || "sd".equalsIgnoreCase(langCode)) {
            playCloudAudio(messageId, speechCleaned, langCode);
            return true;
        }

        // For English, use built-in Android TextToSpeech engine
        if (!isInitialized || tts == null) {
            playCloudAudio(messageId, speechCleaned, langCode);
            return true;
        }

        boolean langSupported = applyLanguage(langCode);
        if (!langSupported) {
            playCloudAudio(messageId, speechCleaned, langCode);
            return true;
        }

        currentlySpeakingMessageId = messageId;
        int queueMode = TextToSpeech.QUEUE_FLUSH;
        int status = tts.speak(speechCleaned, queueMode, null, messageId);

        return (status == TextToSpeech.SUCCESS);
    }

    private void playCloudAudio(String messageId, String text, String langCode) {
        currentlySpeakingMessageId = messageId;
        if (listener != null) {
            listener.onSpeechStarted(messageId);
        }

        apiClient.fetchTtsAudio(text, langCode, new com.agrovision.sindh.ApiClient.AudioCallback() {
            @Override
            public void onSuccess(byte[] audioBytes) {
                try {
                    java.io.File tempMp3 = java.io.File.createTempFile("tts_", ".mp3", context.getCacheDir());
                    tempMp3.deleteOnExit();
                    java.io.FileOutputStream fos = new java.io.FileOutputStream(tempMp3);
                    fos.write(audioBytes);
                    fos.close();

                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        if (!messageId.equals(currentlySpeakingMessageId)) {
                            return; // User stopped or switched message
                        }
                        try {
                            if (mediaPlayer != null) {
                                mediaPlayer.release();
                                mediaPlayer = null;
                            }
                            mediaPlayer = new android.media.MediaPlayer();
                            mediaPlayer.setDataSource(tempMp3.getAbsolutePath());
                            mediaPlayer.setOnCompletionListener(mp -> {
                                currentlySpeakingMessageId = null;
                                if (listener != null) {
                                    listener.onSpeechCompleted(messageId);
                                }
                            });
                            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                                currentlySpeakingMessageId = null;
                                if (listener != null) {
                                    listener.onSpeechError(messageId, "Audio playback error");
                                }
                                return true;
                            });
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (Exception e) {
                            Log.e(TAG, "MediaPlayer start failed", e);
                            fallbackToLocalTts(messageId, text, langCode);
                        }
                    });
                } catch (Exception ex) {
                    Log.e(TAG, "Writing cloud audio failed", ex);
                    fallbackToLocalTts(messageId, text, langCode);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.w(TAG, "Cloud TTS failed: " + errorMessage + ", falling back to local TTS");
                fallbackToLocalTts(messageId, text, langCode);
            }
        });
    }

    private void fallbackToLocalTts(String messageId, String text, String langCode) {
        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
            if (!isInitialized || tts == null) {
                currentlySpeakingMessageId = null;
                if (listener != null) {
                    listener.onSpeechError(messageId, "Speech synthesizer unavailable.");
                }
                return;
            }
            applyLanguage(langCode);
            currentlySpeakingMessageId = messageId;
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, messageId);
        });
    }

    /**
     * Stops any ongoing speech immediately.
     */
    public void stop() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            } catch (Exception ignored) {}
            mediaPlayer = null;
        }

        if (tts != null && isInitialized) {
            try {
                tts.stop();
            } catch (Exception ignored) {}
        }
        currentlySpeakingMessageId = null;
    }

    public boolean isSpeaking() {
        boolean mediaPlaying = false;
        if (mediaPlayer != null) {
            try {
                mediaPlaying = mediaPlayer.isPlaying();
            } catch (Exception ignored) {}
        }
        return mediaPlaying || (tts != null && isInitialized && tts.isSpeaking());
    }

    public String getCurrentlySpeakingMessageId() {
        return currentlySpeakingMessageId;
    }

    /**
     * Releases TextToSpeech resources. Must be called in Activity onDestroy.
     */
    public void shutdown() {
        listener = null;
        if (tts != null) {
            try {
                tts.stop();
                tts.shutdown();
            } catch (Exception ignored) {}
            tts = null;
        }
        isInitialized = false;
        currentlySpeakingMessageId = null;
    }

    /**
     * Cleans emojis and markdown syntax (asterisks, hashtags, bullets) so TTS sounds natural.
     */
    public static String cleanMarkdownForSpeech(String markdown) {
        if (markdown == null) return "";
        return markdown
                .replaceAll("(?m)^[#*\\-]+\\s+", "") // remove markdown list bullets/headers at start of line
                .replaceAll("[*#_~`]", "")           // remove bold, italic, code markers
                .replaceAll("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]", "") // remove emoji surrogate pairs
                .replaceAll("\\s+", " ")             // collapse multi-spaces
                .trim();
    }
}
