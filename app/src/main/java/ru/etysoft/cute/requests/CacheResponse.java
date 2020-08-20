package ru.etysoft.cute.requests;

import ru.etysoft.cute.AppSettings;

public class CacheResponse {

    public static void saveResponseToCache(String url, String response, AppSettings appSettings) {
        appSettings.setString("[URL]" + url, response);
    }

    public static boolean hasCache(String url, AppSettings appSettings) {
        if (appSettings.getString("[URL]" + url) == null) {
            return false;
        } else {
            return true;
        }
    }

    public static String getResponseFromCache(String url, AppSettings appSettings) {
        return appSettings.getString("[URL]" + url);
    }
}
