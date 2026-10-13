package com.agrovision.sindh;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.agrovision.sindh.databinding.ActivityResultBinding;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

/**
 * Displays the prediction result card after image upload.
 * Handles the API call lifecycle: loading → result / error / low_confidence.
 */
public class ResultActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_URI = "extra_image_uri";
    public static final String EXTRA_LANGUAGE = "extra_language";

    private ActivityResultBinding binding;
    private ApiClient apiClient;
    private String currentLanguage = "en";

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.result_title));
        }

        currentLanguage = getIntent().getStringExtra(EXTRA_LANGUAGE);
        if (currentLanguage == null) currentLanguage = "en";

        String imageUriString = getIntent().getStringExtra(EXTRA_IMAGE_URI);
        if (imageUriString == null) {
            finish();
            return;
        }

        Uri imageUri = Uri.parse(imageUriString);
        apiClient = new ApiClient();

        // Show the selected image
        Glide.with(this)
                .load(imageUri)
                .centerCrop()
                .into(binding.ivLeafImage);

        // Start in loading state
        showLoading();

        // Make prediction API call
        apiClient.predict(this, imageUri, new ApiClient.PredictionCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                mainHandler.post(() -> handleSuccess(jsonResponse));
            }

            @Override
            public void onFailure(String errorMessage) {
                mainHandler.post(() -> handleError(errorMessage));
            }
        });

        // Retry button
        binding.btnRetry.setOnClickListener(v -> {
            showLoading();
            apiClient.predict(this, imageUri, new ApiClient.PredictionCallback() {
                @Override
                public void onSuccess(String jsonResponse) {
                    mainHandler.post(() -> handleSuccess(jsonResponse));
                }
                @Override
                public void onFailure(String errorMessage) {
                    mainHandler.post(() -> handleError(errorMessage));
                }
            });
        });
    }

    // ─── UI State: Loading ───────────────────────────────────────────────────
    private void showLoading() {
        binding.layoutLoading.setVisibility(View.VISIBLE);
        binding.layoutResult.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.GONE);
        binding.layoutWarning.setVisibility(View.GONE);
    }

    // ─── Handle Success ──────────────────────────────────────────────────────
    private void handleSuccess(String jsonResponse) {
        try {
            Gson gson = new Gson();
            PredictionResult result = gson.fromJson(jsonResponse, PredictionResult.class);

            if ("error".equals(result.status)) {
                handleError(result.message != null ? result.message : "Unknown server error");
                return;
            }

            showResult(result);
        } catch (Exception e) {
            handleError("Failed to parse server response: " + e.getMessage());
        }
    }

    // ─── Show Result Card ────────────────────────────────────────────────────
    private void showResult(PredictionResult result) {
        binding.layoutLoading.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.GONE);
        binding.layoutResult.setVisibility(View.VISIBLE);

        // ── Crop & Disease
        binding.tvCropName.setText(result.getCrop(currentLanguage));
        binding.tvDiseaseName.setText(result.getDisease(currentLanguage));

        // ── Confidence bar
        int confidenceInt = (int) result.confidence;
        binding.confidenceBar.setProgress(confidenceInt);
        binding.tvConfidencePercent.setText(String.format("%d%%", confidenceInt));

        // ── Confidence color coding
        if (result.isHighConfidence()) {
            if (result.isHealthy()) {
                binding.tvConfidenceLabel.setTextColor(getColor(R.color.color_healthy));
                binding.cardStatus.setCardBackgroundColor(getColor(R.color.color_healthy_bg));
                binding.ivStatusIcon.setImageResource(R.drawable.ic_healthy);
                binding.tvStatusLabel.setText(getString(R.string.status_healthy));
            } else {
                binding.tvConfidenceLabel.setTextColor(getColor(R.color.color_disease));
                binding.cardStatus.setCardBackgroundColor(getColor(R.color.color_disease_bg));
                binding.ivStatusIcon.setImageResource(R.drawable.ic_disease);
                binding.tvStatusLabel.setText(getString(R.string.status_disease_detected));
            }
        }

        // ── Low confidence warning
        if (result.isLowConfidence() && result.warning != null) {
            binding.layoutWarning.setVisibility(View.VISIBLE);
            binding.tvWarningMessage.setText(result.warning);
        } else {
            binding.layoutWarning.setVisibility(View.GONE);
        }

        // ── Advice
        String advice = result.getAdvice(currentLanguage);
        binding.tvAdvice.setText(advice);

        // ── Disclaimer (always shown)
        binding.tvDisclaimer.setText(result.disclaimer);

        // ── Top-3 alternate predictions
        if (result.top3 != null && result.top3.size() >= 3) {
            binding.tvTop1.setText(String.format("%s (%.1f%%)",
                    formatClassName(result.top3.get(0).className), result.top3.get(0).confidence));
            binding.tvTop2.setText(String.format("%s (%.1f%%)",
                    formatClassName(result.top3.get(1).className), result.top3.get(1).confidence));
            binding.tvTop3.setText(String.format("%s (%.1f%%)",
                    formatClassName(result.top3.get(2).className), result.top3.get(2).confidence));
        }

        // ── Entrance animation
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        binding.layoutResult.startAnimation(slideUp);
    }

    // ─── Handle Error ────────────────────────────────────────────────────────
    private void handleError(String errorMessage) {
        binding.layoutLoading.setVisibility(View.GONE);
        binding.layoutResult.setVisibility(View.GONE);
        binding.layoutWarning.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.VISIBLE);
        binding.tvErrorMessage.setText(errorMessage);

        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        binding.layoutError.startAnimation(shake);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────
    private String formatClassName(String className) {
        if (className == null) return "";
        return className.replace("___", " — ").replace("_", " ");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
