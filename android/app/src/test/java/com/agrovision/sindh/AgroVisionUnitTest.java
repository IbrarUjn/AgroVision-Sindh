package com.agrovision.sindh;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for LanguageManager and PredictionResult logic.
 * Run with: ./gradlew test
 */
public class AgroVisionUnitTest {

    @Test
    public void languageManager_isRtl_urduIsRtl() {
        assertTrue("Urdu should be RTL", LanguageManager.isRtl("ur"));
    }

    @Test
    public void languageManager_isRtl_sindhiIsRtl() {
        assertTrue("Sindhi should be RTL", LanguageManager.isRtl("sd"));
    }

    @Test
    public void languageManager_isRtl_englishIsNotRtl() {
        assertFalse("English should not be RTL", LanguageManager.isRtl("en"));
    }

    @Test
    public void languageManager_getLabel_english() {
        assertEquals("English", LanguageManager.getLanguageLabel("en"));
    }

    @Test
    public void languageManager_getLabel_urdu() {
        assertEquals("اردو", LanguageManager.getLanguageLabel("ur"));
    }

    @Test
    public void predictionResult_isHealthy_forHealthyClass() {
        PredictionResult result = new PredictionResult();
        result.className = "Tomato___Healthy";
        assertTrue(result.isHealthy());
    }

    @Test
    public void predictionResult_isHealthy_forDiseaseClass() {
        PredictionResult result = new PredictionResult();
        result.className = "Tomato___Early_blight";
        assertFalse(result.isHealthy());
    }

    @Test
    public void predictionResult_isHighConfidence_forOkStatus() {
        PredictionResult result = new PredictionResult();
        result.status = "ok";
        assertTrue(result.isHighConfidence());
    }

    @Test
    public void predictionResult_isLowConfidence_forLowStatus() {
        PredictionResult result = new PredictionResult();
        result.status = "low_confidence";
        assertTrue(result.isLowConfidence());
        assertFalse(result.isHighConfidence());
    }

    @Test
    public void predictionResult_getDisease_fallsBackToEnglish() {
        PredictionResult result = new PredictionResult();
        result.diseaseEn = "Tomato Early Blight";
        result.diseaseUr = null;
        // Should fall back to English when Urdu is null
        String disease = result.getDisease("ur");
        assertEquals("Tomato Early Blight", disease);
    }

    @Test
    public void predictionResult_getAdvice_returnsCorrectLanguage() {
        PredictionResult result = new PredictionResult();
        result.adviceEn = "Apply fungicide.";
        result.adviceUr = "فنگسائیڈ لگائیں۔";
        result.adviceSd = "فنگيسائيڊ لڳايو۔";
        assertEquals("Apply fungicide.", result.getAdvice("en"));
        assertEquals("فنگسائیڈ لگائیں۔", result.getAdvice("ur"));
        assertEquals("فنگيسائيڊ لڳايو۔", result.getAdvice("sd"));
    }

    @Test
    public void chatMessage_hasDistinctId() {
        ChatMessage msg1 = new ChatMessage("Message 1", ChatMessage.TYPE_ASSISTANT);
        ChatMessage msg2 = new ChatMessage("Message 2", ChatMessage.TYPE_ASSISTANT);
        assertNotNull(msg1.getId());
        assertNotNull(msg2.getId());
        assertNotEquals(msg1.getId(), msg2.getId());
    }

    @Test
    public void ttsManager_cleanMarkdownForSpeech_stripsMarkdownAndBullets() {
        String input = "### Tomato Early Blight\n* Apply **copper fungicide**\n- Keep soil _dry_";
        String cleaned = com.agrovision.sindh.voice.AgroVisionTtsManager.cleanMarkdownForSpeech(input);
        assertEquals("Tomato Early Blight Apply copper fungicide Keep soil dry", cleaned);
    }
}
