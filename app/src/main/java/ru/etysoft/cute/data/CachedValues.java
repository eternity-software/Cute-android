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

    public static String getId(Context context) throws NotCachedException {
        CacheUtils cacheUtils = CacheUtils.getInstance();
        if (!cacheUtils.hasKey(CacheKeys.ID, context)) throw new NotCachedException();
        return cacheUtils.getString(CacheKeys.ID, context);
    }

    public static void setId(Context context, String nickname) {
        CacheUtils cacheUtils = CacheUtils.getInstance();
        cacheUtils.setString(CacheKeys.ID, nickname, context);
    }

    public static String getLogin(Context context) throws NotCachedException {
        CacheUtils cacheUtils = CacheUtils.getInstance();
        if (!cacheUtils.hasKey(CacheKeys.LOGIN, context)) throw new NotCachedException();
        return cacheUtils.getString(CacheKeys.LOGIN, context);
    }

    public static void setLogin(Context context, String nickname) {
        CacheUtils cacheUtils = CacheUtils.getInstance();
        cacheUtils.setString(CacheKeys.LOGIN, nickname, context);
    }

    public static String getDisplayName(Context context) throws NotCachedException {
        CacheUtils cacheUtils = CacheUtils.getInstance();
        if (!cacheUtils.hasKey(CacheKeys.DISPLAY_NAME, context)) throw new NotCachedException();
        return cacheUtils.getString(CacheKeys.DISPLAY_NAME, context);
    }

    public static void setDisplayName(Context context, String displayName) {
        CacheUtils cacheUtils = CacheUtils.getInstance();
        cacheUtils.setString(CacheKeys.DISPLAY_NAME, displayName, context);
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
        public final static String ID = "id";
        public final static String LOGIN = "login";
        public final static String DISPLAY_NAME = "display_name";
        public final static String CUSTOM_LANG = "custom_lang";
    }
}

