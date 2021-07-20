package ru.etysoft.cute;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import ru.etysoft.cute.activities.MainActivity;
import ru.etysoft.cute.data.CacheUtils;

public class SplashScreen extends AppCompatActivity {
    private final static String ISDARK_THEME = "APP_THEME_NIGHT";

    private CacheUtils cacheUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        cacheUtils = CacheUtils.getInstance();

        // Запуск активности
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();

        // Инициализация текущей темы и её применение
        if (cacheUtils.hasKey(ISDARK_THEME, this)) {
            if (cacheUtils.getBoolean(ISDARK_THEME, this)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }
}