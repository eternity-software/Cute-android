package ru.etysoft.cute.requests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;

public class POST {


    private static final MediaType MEDIA_TYPE_FROM_DATA = MediaType.parse("multipart/form-data");



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
