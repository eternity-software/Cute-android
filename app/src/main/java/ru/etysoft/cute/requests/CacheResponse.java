package ru.etysoft.cute.requests;

import ru.etysoft.cute.data.CacheUtils;

public class CacheResponse {

    public static void saveResponseToCache(String url, String response, CacheUtils cacheUtils) {
        // cacheUtils.setString("[URL]" + url, response, this);
    }

    public static boolean hasCache(String url, CacheUtils cacheUtils) {
        return false;
    }

    protected static void deleteResponseFromCache(String url, CacheUtils cacheUtils) {
        saveResponseToCache(url, null, cacheUtils);
    }

    public static String getResponseFromCache(String url, CacheUtils cacheUtils) {
        return null;
    }
}
