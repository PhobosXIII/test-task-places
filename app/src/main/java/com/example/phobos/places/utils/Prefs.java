package com.example.phobos.places.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private static final String PREFS_FILE = "settings";
    private static final String PREFS_SYNC = "sync";

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_FILE, 0);
    }

    public static void markSync(Context context) {
        getPrefs(context).edit().putBoolean(PREFS_SYNC, true).apply();
    }

    public static boolean isSync(Context context) {
        return getPrefs(context).getBoolean(PREFS_SYNC, false);
    }
}
