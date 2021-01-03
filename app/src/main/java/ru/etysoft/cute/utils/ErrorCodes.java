package ru.etysoft.cute.utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ru.etysoft.cute.api.Methods;

public class ErrorCodes {

    private static Map<String, String> codes = new HashMap<String, String>();

    public static void initialize(final Context context) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = Methods.getErrorCodes(context);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        String code = data.getString("code");
                        String info = data.getString("data");
                        codes.put(code, info);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    public static String getError(String code) {

        String error = codes.get(code);
        if (error == null) {
            return code;
        }
        return error;
    }

}
