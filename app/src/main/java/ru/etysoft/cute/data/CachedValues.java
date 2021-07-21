package ru.etysoft.cute.data;

import android.content.Context;

import ru.etysoft.cute.exceptions.NotCachedException;

public class CachedValues {

    public static String getSessionKey(Context context) throws NotCachedException {
        CacheUtils cacheUtils = CacheUtils.getInstance();
        if (!cacheUtils.hasKey(CacheKeys.SESSION_KEY, context)) throw new NotCachedException();
        return cacheUtils.getString(CacheKeys.SESSION_KEY, context);
    }

    public static void setSessionKey(Context context, String sessionKey) {
        CacheUtils cacheUtils = CacheUtils.getInstance();
        cacheUtils.setString(CacheKeys.SESSION_KEY, sessionKey, context);
    }

    public static String getEmail(Context context) throws NotCachedException {
        CacheUtils cacheUtils = CacheUtils.getInstance();
        if (!cacheUtils.hasKey(CacheKeys.EMAIL, context)) throw new NotCachedException();
        return cacheUtils.getString(CacheKeys.EMAIL, context);
    }

    public static void setEmail(Context context, String email) {
        CacheUtils cacheUtils = CacheUtils.getInstance();
        cacheUtils.setString(CacheKeys.EMAIL, email, context);
    }

    public static String getCustomLanguage(Context context) throws NotCachedException {
        CacheUtils cacheUtils = CacheUtils.getInstance();
        if (!cacheUtils.hasKey(CacheKeys.CUSTOM_LANG, context)) throw new NotCachedException();
        return cacheUtils.getString(CacheKeys.CUSTOM_LANG, context);
    }

    public static void setCustomLanguage(Context context, String email) {
        CacheUtils cacheUtils = CacheUtils.getInstance();
        cacheUtils.setString(CacheKeys.CUSTOM_LANG, email, context);
    }

    public static void removeCustomLanguage(Context context) {
        CacheUtils cacheUtils = CacheUtils.getInstance();
        cacheUtils.removeString(CacheKeys.CUSTOM_LANG, context);
    }

    public class CacheKeys {
        public final static String SESSION_KEY = "session";
        public final static String EMAIL = "email";
        public final static String CUSTOM_LANG = "custom_lang";
    }
}

