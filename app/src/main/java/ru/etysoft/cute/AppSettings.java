package ru.etysoft.cute;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

class AppSettings {

    // Получение строки по ключу
    public String getString(String name)
    {
        SharedPreferences pref = context.getSharedPreferences("s", Context.MODE_PRIVATE);
        return pref.getString(name, null);
    }

    // Запись строки по ключу
    public void setString(String key, String text)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences("s", Context.MODE_PRIVATE).edit();
        editor.putString(key, text);

        editor.apply();
    }

    // Получение boolean по ключу
    public boolean getBoolean(String name)
    {
        SharedPreferences pref = context.getSharedPreferences("s", Context.MODE_PRIVATE);
        return pref.getBoolean(name, false);
    }

    // Запись boolean по ключу
    public void setBoolean(String key, Boolean bool)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences("s", Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, bool);
        editor.apply();
    }

    private Context context;

    // Инициализация контекста
    public AppSettings(Context contexta){
        context = contexta;
    }


}
