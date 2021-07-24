package ru.etysoft.cute.data;

import android.content.Context;
import android.content.SharedPreferences;

public class CacheUtils {

    private final static String IDENTIFIER = "APP_SETTINGS";
    private static CacheUtils instance;


    // Инициализация контекста
    public static CacheUtils getInstance() {
        if (instance == null) {
            instance = new CacheUtils();
        }
        return instance;
    }

    // Запись строки по ключу
    public void setString(String key, String text, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(IDENTIFIER, Context.MODE_PRIVATE).edit();
        editor.putString(key, text);
        editor.apply();
    }

    public boolean hasKey(String key, Context context) {
        SharedPreferences pref = context.getSharedPreferences(IDENTIFIER, Context.MODE_PRIVATE);
        return pref.contains(key);
    }

    // Получение boolean по ключу
    public boolean getBoolean(String name, Context context) {
        SharedPreferences pref = context.getSharedPreferences(IDENTIFIER, Context.MODE_PRIVATE);
        return pref.getBoolean(name, false);
    }

    // Получение строки по ключу
    public String getString(String name, Context context) {
        SharedPreferences pref = context.getSharedPreferences(IDENTIFIER, Context.MODE_PRIVATE);
        return pref.getString(name, null);
    }

    // Запись boolean по ключу
    public void setBoolean(String key, Boolean bool, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(IDENTIFIER, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, bool);
        editor.apply();
    }


    // Стереть все сохранённые данные
    public void clean(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(IDENTIFIER, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }


}
