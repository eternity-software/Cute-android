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

    public class CacheKeys {
        public final static String SESSION_KEY = "session";
    }
}

