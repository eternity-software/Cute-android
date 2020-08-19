package ru.etysoft.cute.utils;

import android.util.Log;

public class Logger {

    public static void logRequest(String type, String text) {
        Log.d("[" + type + "]", text);
    }

    public static void logResponse(String text) {
        Log.d("[RESPONSE]", text);
    }

    public static void logReceiver(String text) {
        Log.d("[RECEIVE]", text);
    }


}
