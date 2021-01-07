package ru.etysoft.cute.requests;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class POST {

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final MediaType MEDIA_TYPE_FROM_DATA = MediaType.parse("multipart/form-data");

    public static String executeImage(String url, File file) {
        try {
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("title", "Square Logo")
                    .addFormDataPart("image", "logo-square.png",
                            RequestBody.create(MEDIA_TYPE_PNG, file))
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                return response.body().string();
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String executeFromData(String request, String params) {
        String response = "";
        try {
            URL urladress = new URL(request);

            HttpURLConnection conn = (HttpURLConnection) urladress.openConnection();
            conn.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            writer.write(params);
            writer.flush();


            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response = response + line;
            }
            writer.close();
            reader.close();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return response;
        }
    }


}
