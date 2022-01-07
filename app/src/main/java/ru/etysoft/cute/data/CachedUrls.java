package ru.etysoft.cute.data;

import android.content.Context;

import ru.etysoft.cute.exceptions.NotCachedException;

public class CachedUrls {


    public static String getThemeUrl(Context context) throws NotCachedException {
        CacheUtils cacheUtils = CacheUtils.getInstance();
        if (!cacheUtils.hasKey(CacheKeys.CUSTOM_THEME, context)) throw new NotCachedException();
        return cacheUtils.getString(CacheKeys.CUSTOM_THEME, context);
    }

    public static void setThemeUrl(Context context, String email) {
        CacheUtils cacheUtils = CacheUtils.getInstance();
        cacheUtils.setString(CacheKeys.CUSTOM_THEME, email, context);
    }

    public static String getLangUrl(Context context) throws NotCachedException {
        CacheUtils cacheUtils = CacheUtils.getInstance();
        if (!cacheUtils.hasKey(CacheKeys.CUSTOM_LANG, context)) throw new NotCachedException();
        return cacheUtils.getString(CacheKeys.CUSTOM_LANG, context);
    }

    public static void setLangUrl(Context context, String email) {
        CacheUtils cacheUtils = CacheUtils.getInstance();
        cacheUtils.setString(CacheKeys.CUSTOM_LANG, email, context);
    }

    public class CacheKeys {
        public final static String CUSTOM_LANG = "url_lang";
        public final static String CUSTOM_THEME = "url_theme";
    }
}
