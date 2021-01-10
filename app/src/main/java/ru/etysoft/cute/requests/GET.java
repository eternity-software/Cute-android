package ru.etysoft.cute.requests;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.etysoft.cute.utils.Logger;

public class GET {

    public static String execute(String url) {
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                String resp = response.body().string();
                Logger.logResponse(resp, "UNKNOWN");
                return resp;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String executeNoTimeout(String url) {
        try {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS).build();

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                String resp = response.body().string();
                Logger.logResponse(resp, "UNKNOWN");
                return resp;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
