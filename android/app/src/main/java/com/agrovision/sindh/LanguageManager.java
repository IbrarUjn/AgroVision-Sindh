package com.agrovision.sindh;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * Handles language switching between English, Urdu, and Sindhi.
 * Saves preference in SharedPreferences and updates app locale.
 */
public class LanguageManager {

    private static final String PREFS_NAME = "agrovision_prefs";
    private static final String KEY_LANGUAGE = "selected_language";
    private static final String KEY_AUTO_VOICE = "auto_voice_output";
    private static final String DEFAULT_LANGUAGE = "en";

    /** Save language preference */
    public static void saveLanguage(Context context, String langCode) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LANGUAGE, langCode).apply();
    }

    /** Get saved language preference */
    public static String getSavedLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE);
    }

    /** Save auto-voice output toggle state (default: true) */
    public static void saveAutoVoice(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_AUTO_VOICE, enabled).apply();
    }

    /** Get saved auto-voice output toggle state (default: true) */
    public static boolean isAutoVoiceEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_AUTO_VOICE, true);
    }

    /**
     * Apply locale to context (call before setContentView or before recreate()).
     * For Urdu and Sindhi, also sets RTL layout direction.
     */
    public static void applyLanguage(Context context, String langCode) {
        Locale locale;
        switch (langCode) {
            case "ur":
                locale = new Locale("ur", "PK");
                break;
            case "sd":
                locale = new Locale("sd", "PK");
                break;
            default:
                locale = Locale.ENGLISH;
                break;
        }
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        if (langCode.equals("ur") || langCode.equals("sd")) {
            config.setLayoutDirection(locale); // RTL
        } else {
            config.setLayoutDirection(Locale.ENGLISH); // LTR
        }
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
    }

    /** Returns the human-readable language label for a code */
    public static String getLanguageLabel(String langCode) {
        switch (langCode) {
            case "ur": return "اردو";
            case "sd": return "سنڌي";
            default:   return "English";
        }
    }

    /** Returns true if language is RTL */
    public static boolean isRtl(String langCode) {
        return langCode.equals("ur") || langCode.equals("sd");
    }
}
