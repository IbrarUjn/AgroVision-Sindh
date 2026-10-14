package com.agrovision.sindh;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrovision.sindh.databinding.ActivityChatBinding;
import com.agrovision.sindh.voice.AgroVisionSpeechManager;
import com.agrovision.sindh.voice.AgroVisionTtsManager;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * AgroVision AI Farmer Assistant Chat Screen.
 * Provides interactive, context-aware agricultural guidance with two-way voice interaction
 * in English, Urdu, and Sindhi.
 */
public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_LANGUAGE = "extra_language";
    public static final String EXTRA_CLASS_NAME = "extra_class_name";
    public static final String EXTRA_CROP_EN = "extra_crop_en";
    public static final String EXTRA_CROP_UR = "extra_crop_ur";
    public static final String EXTRA_CROP_SD = "extra_crop_sd";
    public static final String EXTRA_DISEASE_EN = "extra_disease_en";
    public static final String EXTRA_DISEASE_UR = "extra_disease_ur";
    public static final String EXTRA_DISEASE_SD = "extra_disease_sd";
    public static final String EXTRA_CONFIDENCE = "extra_confidence";
    public static final String EXTRA_ADVICE_EN = "extra_advice_en";
    public static final String EXTRA_ADVICE_UR = "extra_advice_ur";
    public static final String EXTRA_ADVICE_SD = "extra_advice_sd";

    private ActivityChatBinding binding;
    private ChatAdapter chatAdapter;
    private final List<ChatMessage> messageList = new ArrayList<>();
    private final List<ChatRequest.HistoryItem> conversationHistory = new ArrayList<>();

    private ApiClient apiClient;
    private String currentLanguage = "en";
    private Map<String, Object> predictionContext = null;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private String lastFailedMessage = null;

    // Voice interaction managers
    private AgroVisionSpeechManager speechManager;
    private AgroVisionTtsManager ttsManager;
    private boolean isAutoVoiceEnabled = true;
    private final Set<String> spokenMessageIds = new HashSet<>();

    // Audio permission request launcher
    private final ActivityResultLauncher<String> recordAudioLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startVoiceRecognition();
                } else {
                    Toast.makeText(this, getString(R.string.voice_error_permission_denied), Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentLanguage = getIntent().getStringExtra(EXTRA_LANGUAGE);
        if (currentLanguage == null || currentLanguage.isEmpty()) {
            currentLanguage = LanguageManager.getSavedLanguage(this);
        }

        isAutoVoiceEnabled = LanguageManager.isAutoVoiceEnabled(this);
        apiClient = new ApiClient();

        setupVoiceManagers();
        setupToolbar();
        setupRecyclerView();
        extractPredictionContext();
        setupSuggestedChips();
        setupInput();

        // Initial welcome message
        addInitialWelcome();
    }

    private void setupVoiceManagers() {
        // Speech-to-Text Manager
        speechManager = new AgroVisionSpeechManager(this, new AgroVisionSpeechManager.SpeechStateListener() {
            @Override
            public void onListeningStarted() {
                mainHandler.post(() -> {
                    binding.layoutListening.setVisibility(View.VISIBLE);
                    binding.tvListeningStatus.setText(getString(R.string.voice_listening));
                    binding.btnMic.setEnabled(false);
                });
            }

            @Override
            public void onListeningFinished() {
                mainHandler.post(() -> {
                    binding.layoutListening.setVisibility(View.GONE);
                    binding.btnMic.setEnabled(true);
                });
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // Audio level feedback
            }

            @Override
            public void onPartialTranscription(String partialText) {
                mainHandler.post(() -> {
                    if (!TextUtils.isEmpty(partialText)) {
                        binding.etMessage.setText(partialText);
                        binding.etMessage.setSelection(partialText.length());
                    }
                });
            }

            @Override
            public void onFinalTranscription(String finalText) {
                mainHandler.post(() -> {
                    if (!TextUtils.isEmpty(finalText)) {
                        binding.etMessage.setText(finalText);
                        binding.etMessage.setSelection(finalText.length());
                    }
                });
            }

            @Override
            public void onSpeechError(String errorMessage) {
                mainHandler.post(() -> {
                    binding.layoutListening.setVisibility(View.GONE);
                    binding.btnMic.setEnabled(true);
                    Toast.makeText(ChatActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });

        // Text-to-Speech Manager
        ttsManager = new AgroVisionTtsManager(this, new AgroVisionTtsManager.TtsStateListener() {
            @Override
            public void onTtsInitialized(boolean success) {
                if (!success) {
                    mainHandler.post(() -> {
                        // Inform user gracefully if TTS cannot initialize
                    });
                }
            }

            @Override
            public void onSpeechStarted(String messageId) {
                mainHandler.post(() -> {
                    if (chatAdapter != null) {
                        chatAdapter.setCurrentlySpeakingMessageId(messageId);
                    }
                });
            }

            @Override
            public void onSpeechCompleted(String messageId) {
                mainHandler.post(() -> {
                    if (chatAdapter != null) {
                        chatAdapter.setCurrentlySpeakingMessageId(null);
                    }
                });
            }

            @Override
            public void onSpeechError(String messageId, String errorDescription) {
                mainHandler.post(() -> {
                    if (chatAdapter != null) {
                        chatAdapter.setCurrentlySpeakingMessageId(null);
                    }
                    if (errorDescription != null && !errorDescription.isEmpty()) {
                        Toast.makeText(ChatActivity.this, errorDescription, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.chat_title));
            getSupportActionBar().setSubtitle(getString(R.string.chat_subtitle));
        }
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(messageList, failedMessage -> {
            // Retry handler
            if (lastFailedMessage != null) {
                sendMessage(lastFailedMessage);
            }
        }, messageToSpeak -> {
            // Speaker icon tapped: toggle playback
            if (ttsManager != null) {
                if (ttsManager.isSpeaking() && messageToSpeak.getId().equals(ttsManager.getCurrentlySpeakingMessageId())) {
                    ttsManager.stop();
                    chatAdapter.setCurrentlySpeakingMessageId(null);
                } else {
                    speakMessage(messageToSpeak);
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.recyclerChat.setLayoutManager(layoutManager);
        binding.recyclerChat.setAdapter(chatAdapter);
    }

    private void extractPredictionContext() {
        String className = getIntent().getStringExtra(EXTRA_CLASS_NAME);
        if (className != null && !className.isEmpty()) {
            predictionContext = new HashMap<>();
            predictionContext.put("class_name", className);
            predictionContext.put("crop_en", getIntent().getStringExtra(EXTRA_CROP_EN));
            predictionContext.put("crop_ur", getIntent().getStringExtra(EXTRA_CROP_UR));
            predictionContext.put("crop_sd", getIntent().getStringExtra(EXTRA_CROP_SD));
            predictionContext.put("disease_en", getIntent().getStringExtra(EXTRA_DISEASE_EN));
            predictionContext.put("disease_ur", getIntent().getStringExtra(EXTRA_DISEASE_UR));
            predictionContext.put("disease_sd", getIntent().getStringExtra(EXTRA_DISEASE_SD));
            predictionContext.put("confidence", getIntent().getFloatExtra(EXTRA_CONFIDENCE, 0f));
            predictionContext.put("advice_en", getIntent().getStringExtra(EXTRA_ADVICE_EN));
            predictionContext.put("advice_ur", getIntent().getStringExtra(EXTRA_ADVICE_UR));
            predictionContext.put("advice_sd", getIntent().getStringExtra(EXTRA_ADVICE_SD));

            // Show active context badge
            binding.cardPredictionContext.setVisibility(View.VISIBLE);
            String localizedDisease = getLocalizedDisease();
            float conf = getIntent().getFloatExtra(EXTRA_CONFIDENCE, 0f);
            binding.tvContextTitle.setText(String.format("%s (%.0f%%)", localizedDisease, conf));
        } else {
            binding.cardPredictionContext.setVisibility(View.GONE);
        }
    }

    private String getLocalizedDisease() {
        if (predictionContext == null) return "";
        if ("ur".equals(currentLanguage)) {
            Object val = predictionContext.get("disease_ur");
            if (val != null) return val.toString();
        } else if ("sd".equals(currentLanguage)) {
            Object val = predictionContext.get("disease_sd");
            if (val != null) return val.toString();
        }
        Object val = predictionContext.get("disease_en");
        return val != null ? val.toString() : "";
    }

    private String getLocalizedCrop() {
        if (predictionContext == null) return "";
        if ("ur".equals(currentLanguage)) {
            Object val = predictionContext.get("crop_ur");
            if (val != null) return val.toString();
        } else if ("sd".equals(currentLanguage)) {
            Object val = predictionContext.get("crop_sd");
            if (val != null) return val.toString();
        }
        Object val = predictionContext.get("crop_en");
        return val != null ? val.toString() : "";
    }

    private void addInitialWelcome() {
        String welcomeText;
        if (predictionContext != null) {
            String crop = getLocalizedCrop();
            String disease = getLocalizedDisease();
            welcomeText = getString(R.string.chat_empty_welcome_with_scan, crop, disease);
        } else {
            welcomeText = getString(R.string.chat_empty_welcome);
        }
        messageList.add(new ChatMessage(welcomeText, ChatMessage.TYPE_ASSISTANT));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        binding.recyclerChat.scrollToPosition(messageList.size() - 1);
    }

    private void setupSuggestedChips() {
        binding.chipGroupSuggestions.removeAllViews();

        List<String> suggestions = new ArrayList<>();
        if (predictionContext != null) {
            suggestions.add(getString(R.string.chip_what_does_it_mean));
            suggestions.add(getString(R.string.chip_how_to_prevent));
            suggestions.add(getString(R.string.chip_next_steps));
            suggestions.add(getString(R.string.chip_protect_healthy));
        } else {
            suggestions.add(getString(R.string.chip_why_yellow));
            suggestions.add(getString(R.string.chip_protect_healthy));
            suggestions.add(getString(R.string.chip_how_to_prevent));
        }

        for (String text : suggestions) {
            Chip chip = new Chip(this);
            chip.setText(text);
            chip.setTextColor(getColor(R.color.white));
            chip.setChipBackgroundColorResource(R.color.card_glass);
            chip.setChipStrokeColorResource(R.color.white_20);
            chip.setChipStrokeWidth(1f);
            chip.setOnClickListener(v -> {
                binding.etMessage.setText(text);
                sendMessage(text);
            });
            binding.chipGroupSuggestions.addView(chip);
        }
    }

    private void setupInput() {
        binding.btnMic.setOnClickListener(v -> checkAndStartVoiceRecognition());

        binding.btnCancelListening.setOnClickListener(v -> {
            if (speechManager != null) {
                speechManager.cancel();
            }
        });

        binding.btnSend.setOnClickListener(v -> {
            String text = binding.etMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                sendMessage(text);
            }
        });
    }

    private void checkAndStartVoiceRecognition() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            startVoiceRecognition();
        } else {
            recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    private void startVoiceRecognition() {
        if (speechManager != null) {
            // Stop any ongoing TTS before listening
            if (ttsManager != null && ttsManager.isSpeaking()) {
                ttsManager.stop();
                chatAdapter.setCurrentlySpeakingMessageId(null);
            }
            speechManager.startListening(currentLanguage);
        }
    }

    private void speakMessage(ChatMessage message) {
        if (ttsManager == null || message == null || message.isError()) return;
        ttsManager.speak(message.getId(), message.getText(), currentLanguage);
    }

    private void sendMessage(String userText) {
        // Stop any active speech recognition or speech output
        if (speechManager != null && speechManager.isListening()) {
            speechManager.stopListening();
        }
        if (ttsManager != null && ttsManager.isSpeaking()) {
            ttsManager.stop();
            chatAdapter.setCurrentlySpeakingMessageId(null);
        }

        // Clear input field
        binding.etMessage.setText("");

        // Dismiss keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        // Add user message to UI
        messageList.add(new ChatMessage(userText, ChatMessage.TYPE_USER));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        binding.recyclerChat.scrollToPosition(messageList.size() - 1);

        // Show typing indicator
        binding.layoutTyping.setVisibility(View.VISIBLE);
        binding.btnSend.setEnabled(false);

        // Build API request
        ChatRequest request = new ChatRequest();
        request.message = userText;
        request.language = currentLanguage;
        request.predictionContext = predictionContext;
        request.history = new ArrayList<>(conversationHistory);

        // Record for retry
        lastFailedMessage = userText;

        apiClient.sendChat(request, new ApiClient.ChatCallback() {
            @Override
            public void onSuccess(ChatResponse chatResponse) {
                mainHandler.post(() -> {
                    if (isFinishing() || isDestroyed()) return;

                    binding.layoutTyping.setVisibility(View.GONE);
                    binding.btnSend.setEnabled(true);
                    lastFailedMessage = null;

                    String reply = chatResponse.reply != null ? chatResponse.reply : "Guidance received.";
                    
                    // Add assistant response to UI
                    ChatMessage assistantMsg = new ChatMessage(reply, ChatMessage.TYPE_ASSISTANT);
                    messageList.add(assistantMsg);
                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                    binding.recyclerChat.scrollToPosition(messageList.size() - 1);

                    // Update memory (bounded history)
                    conversationHistory.add(new ChatRequest.HistoryItem("user", userText));
                    conversationHistory.add(new ChatRequest.HistoryItem("model", reply));
                    if (conversationHistory.size() > 6) {
                        conversationHistory.remove(0);
                        conversationHistory.remove(0);
                    }

                    // Trigger automatic voice output if enabled (exactly once per message)
                    if (isAutoVoiceEnabled && !spokenMessageIds.contains(assistantMsg.getId())) {
                        spokenMessageIds.add(assistantMsg.getId());
                        speakMessage(assistantMsg);
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                mainHandler.post(() -> {
                    if (isFinishing() || isDestroyed()) return;

                    binding.layoutTyping.setVisibility(View.GONE);
                    binding.btnSend.setEnabled(true);

                    // Add error message with retry affordance
                    String displayError = "⚠️ " + errorMessage;
                    messageList.add(new ChatMessage(displayError, ChatMessage.TYPE_ASSISTANT, true));
                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                    binding.recyclerChat.scrollToPosition(messageList.size() - 1);
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Auto-voice toggle action
        int voiceIcon = isAutoVoiceEnabled ? R.drawable.ic_volume_up : R.drawable.ic_volume_off;
        String voiceTitle = isAutoVoiceEnabled ? getString(R.string.voice_auto_tts_on) : getString(R.string.voice_auto_tts_off);
        menu.add(0, 2, 0, voiceTitle)
                .setIcon(voiceIcon)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(0, 1, 1, getString(R.string.menu_clear_chat))
                .setIcon(R.drawable.ic_delete)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == 2) {
            // Toggle Auto-voice
            isAutoVoiceEnabled = !isAutoVoiceEnabled;
            LanguageManager.saveAutoVoice(this, isAutoVoiceEnabled);
            if (!isAutoVoiceEnabled && ttsManager != null && ttsManager.isSpeaking()) {
                ttsManager.stop();
                chatAdapter.setCurrentlySpeakingMessageId(null);
            }
            Toast.makeText(this, isAutoVoiceEnabled ? getString(R.string.voice_auto_tts_on) : getString(R.string.voice_auto_tts_off), Toast.LENGTH_SHORT).show();
            invalidateOptionsMenu();
            return true;
        } else if (item.getItemId() == 1) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.menu_clear_chat))
                    .setMessage("Clear conversation history?")
                    .setPositiveButton("Clear", (dialog, which) -> {
                        if (ttsManager != null) {
                            ttsManager.stop();
                        }
                        spokenMessageIds.clear();
                        messageList.clear();
                        conversationHistory.clear();
                        addInitialWelcome();
                        chatAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause or stop voice input/output when leaving screen
        if (speechManager != null && speechManager.isListening()) {
            speechManager.cancel();
        }
        if (ttsManager != null && ttsManager.isSpeaking()) {
            ttsManager.stop();
            if (chatAdapter != null) {
                chatAdapter.setCurrentlySpeakingMessageId(null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechManager != null) {
            speechManager.destroy();
            speechManager = null;
        }
        if (ttsManager != null) {
            ttsManager.shutdown();
            ttsManager = null;
        }
    }
}
