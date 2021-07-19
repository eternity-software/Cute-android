package ru.etysoft.cute.utils;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;


public class ErrorCodes {

    private static Map<String, String> codes = new HashMap<String, String>();

    public static void initialize(final Context context) {

    }


    public static String getError(String code) {

        String error = codes.get(code);
        if (error == null) {
            return code;
        }
        return error;
    }

}
