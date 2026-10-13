package com.agrovision.sindh;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.agrovision.sindh.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 101;

    private ActivityMainBinding binding;
    private Uri cameraImageUri;
    private String currentLanguage = "en"; // default: English

    // ─── Activity result launchers ───────────────────────────────────────────
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && cameraImageUri != null) {
                    startAnalysis(cameraImageUri);
                }
            });

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedUri = result.getData().getData();
                    if (selectedUri != null) {
                        startAnalysis(selectedUri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Load saved language preference
        currentLanguage = LanguageManager.getSavedLanguage(this);
        LanguageManager.applyLanguage(this, currentLanguage);

        setupUI();
        animateEntrance();
    }

    private void setupUI() {
        // Camera button
        binding.btnCamera.setOnClickListener(v -> {
            animateButton(v);
            checkCameraPermissionAndOpen();
        });

        // Gallery button
        binding.btnGallery.setOnClickListener(v -> {
            animateButton(v);
            checkStoragePermissionAndOpen();
        });

        // Info card — supported crops
        binding.btnSupportedCrops.setOnClickListener(v -> showSupportedCropsDialog());
    }

    private void animateEntrance() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        binding.ivLogo.startAnimation(fadeIn);
        binding.tvTitle.startAnimation(fadeIn);
        binding.tvSubtitle.startAnimation(fadeIn);
        binding.cardButtons.startAnimation(slideUp);
        binding.cardDisclaimer.startAnimation(slideUp);
    }

    private void animateButton(View v) {
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        v.startAnimation(pulse);
    }

    // ─── Camera ──────────────────────────────────────────────────────────────
    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = createImageFile();
        if (photoFile != null) {
            cameraImageUri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            cameraLauncher.launch(takePictureIntent);
        }
    }

    private File createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(new Date());
            String imageFileName = "AGROVISION_" + timeStamp;
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // ─── Gallery ─────────────────────────────────────────────────────────────
    private void checkStoragePermissionAndOpen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_STORAGE_PERMISSION);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    // ─── Navigate to ResultActivity ──────────────────────────────────────────
    private void startAnalysis(Uri imageUri) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, imageUri.toString());
        intent.putExtra(ResultActivity.EXTRA_LANGUAGE, currentLanguage);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // ─── Supported crops dialog ──────────────────────────────────────────────
    private void showSupportedCropsDialog() {
        String crops = "🍎 Apple  •  🫐 Blueberry  •  🍒 Cherry\n" +
                "🌽 Corn (Maize)  •  🍇 Grape  •  🍊 Orange\n" +
                "🍑 Peach  •  🫑 Bell Pepper  •  🥔 Potato\n" +
                "🫐 Raspberry  •  🫘 Soybean  •  🎃 Squash\n" +
                "🍓 Strawberry  •  🍅 Tomato\n\n" +
                "14 crops • 38 disease classes\n" +
                "Model accuracy: 98.75%";

        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle("🌿 Supported Crops")
                .setMessage(crops)
                .setPositiveButton("OK", null)
                .show();
    }

    // ─── Language menu ───────────────────────────────────────────────────────
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_lang_en) {
            setLanguage("en");
            return true;
        } else if (id == R.id.menu_lang_ur) {
            setLanguage("ur");
            return true;
        } else if (id == R.id.menu_lang_sd) {
            setLanguage("sd");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setLanguage(String lang) {
        currentLanguage = lang;
        LanguageManager.saveLanguage(this, lang);
        LanguageManager.applyLanguage(this, lang);
        recreate(); // Reload activity with new locale
    }

    // ─── Permission results ──────────────────────────────────────────────────
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Storage permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
