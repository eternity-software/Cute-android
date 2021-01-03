package ru.etysoft.cute.utils;

import android.util.Log;

public class Logger {

    public static void logRequest(String type, String text) {
        Log.d("[" + type + "]", text);
    }

    public static void logResponse(String text, String method) {
        Log.d("[RESPONSE]", "[" + method + "]: " + text);
    }

    public static void logCache(String text) {
        Log.d("[CACHE]", text);
    }

    public static void logReceiver(String text) {
        Log.d("[RECEIVE]", text);
    }

    public static void logActivity(String text) {
        Log.d("[ACTIVITY]", text);
    }


}
