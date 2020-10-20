package ru.etysoft.cute.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.BuildConfig;
import ru.etysoft.cute.R;

public class SettingsActivity extends AppCompatActivity {

    public static int count = 0;
    private static String ISDARK_THEME = "APP_THEME_NIGHT";
    private static AppSettings appSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        appSettings = new AppSettings(this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (appSettings.getBoolean(ISDARK_THEME)) {

            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {


            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }
    }

    public void changeTheme(View v) {
        //Поверка какая тема сейчас стоит

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Preference verison = findPreference("version");
            verison.setSummary(BuildConfig.VERSION_NAME);
            verison.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    count++;
                    if (count > 10) {
                        count = 0;
                        Intent egg = new Intent(getActivity(), EasterEgg.class);
                        getActivity().startActivity(egg);
                    }
                    return false;
                }
            });


            Preference changePassword = findPreference("changepass");
            changePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent passwordChangeIntent = new Intent(getActivity(), passwordchaange.class);
                    getActivity().startActivity(passwordChangeIntent);
                    getActivity().overridePendingTransition(R.anim.slide_to_right, R.anim.slide_to_left);
                    return false;
                }
            });


            final SwitchPreferenceCompat switchPreference = (SwitchPreferenceCompat) findPreference("APP_THEME_NIGHT");
            switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (((boolean) newValue)) {
                        appSettings.setBoolean(ISDARK_THEME, true);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        appSettings.setBoolean(ISDARK_THEME, false);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    switchPreference.setEnabled(false);
                    // Пересоздание активности для красивой смены темы
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    getActivity().finish();

                    return true;
                }
            });
        }
    }
}