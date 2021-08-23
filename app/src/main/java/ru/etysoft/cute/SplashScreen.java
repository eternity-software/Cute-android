package ru.etysoft.cute;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import ru.etysoft.cute.activities.main.MainActivity;
import ru.etysoft.cute.components.CuteToast;
import ru.etysoft.cute.data.CacheUtils;
import ru.etysoft.cute.exceptions.LanguageParsingException;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.lang.CustomLanguage;

public class SplashScreen extends AppCompatActivity {
    private final static String ISDARK_THEME = "APP_THEME_NIGHT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        CacheUtils cacheUtils = CacheUtils.getInstance();

        try {
            CustomLanguage.loadExisting(this);
        } catch (NotCachedException ignored) {
        } catch (LanguageParsingException e) {
            CuteToast.show(getResources().getString(R.string.err_lang), R.drawable.icon_error, this);
        }

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