package ru.etysoft.cute;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

class AppSettings {
    public String getString(String name)
    {
        SharedPreferences pref = context.getSharedPreferences("s", Context.MODE_PRIVATE);
        return pref.getString(name, null);
    }

    public void setString(String key, String text)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences("s", Context.MODE_PRIVATE).edit();
        editor.putString(key, text);

        editor.apply();
    }

    public boolean getBoolean(String name)
    {
        SharedPreferences pref = context.getSharedPreferences("s", Context.MODE_PRIVATE);
        return pref.getBoolean(name, false);
    }

    public void setBoolean(String key, Boolean bool)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences("s", Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, bool);
        editor.apply();
    }

    private Context context;

    public AppSettings(Context contexta){
        context = contexta;
    }


}
