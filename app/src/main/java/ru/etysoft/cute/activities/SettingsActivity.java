package ru.etysoft.cute.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import ru.etysoft.cute.AlertDialog;
import ru.etysoft.cute.BuildConfig;
import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.meet.MeetActivity;
import ru.etysoft.cute.data.CacheUtils;
import ru.etysoft.cute.themes.Theme;

public class SettingsActivity extends AppCompatActivity {

    public static int count = 0;
    private static final String ISDARK_THEME = "APP_THEME_NIGHT";
    private static CacheUtils cacheUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        cacheUtils = CacheUtils.getInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        AlertDialog alertDialog = new AlertDialog(this, "Кастомная тема", "Вы можете применить кастомную тему", new AlertDialog.DialogHandler() {
            @Override
            public void onPositiveClicked(String input) {
                Theme.loadFromUrl(input, SettingsActivity.this, false);
            }

            @Override
            public void onNegativeClicked(String input) {

            }

            @Override
            public void onClosed(String input) {

            }
        });
        alertDialog.show();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (cacheUtils.hasKey(ISDARK_THEME, this)) {
            if (cacheUtils.getBoolean(ISDARK_THEME, this)) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings_preferences, rootKey);

            Preference verison = findPreference("version");
            verison.setSummary(BuildConfig.VERSION_NAME + " (API " + getString(R.string.api_v) + ")");
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
                    Intent passwordChangeIntent = new Intent(getActivity(), PasswordChange.class);
                    getActivity().startActivity(passwordChangeIntent);
                    getActivity().overridePendingTransition(R.anim.slide_to_right, R.anim.slide_to_left);
                    return false;
                }
            });

            Preference logout = findPreference("logout");
            logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    AlertDialog alertDialog = new AlertDialog(getActivity(), getResources().getString(R.string.logout_title), getString(R.string.logout_text),
                            new AlertDialog.DialogHandler() {
                                @Override
                                public void onPositiveClicked(String input) {
                                    cacheUtils.clean(getContext());
                                    Intent meet = new Intent(getActivity(), MeetActivity.class);
                                    getActivity().startActivity(meet);
                                }

                                @Override
                                public void onNegativeClicked(String input) {

                                }

                                @Override
                                public void onClosed(String input) {

                                }
                            });
                    alertDialog.show();
                    return false;
                }
            });


            Preference debugbutton = findPreference("debug_button");
            debugbutton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), stock.class);
                    startActivity(intent);
                    return false;
                }
            });

            final SwitchPreferenceCompat switchPreference = findPreference("APP_THEME_NIGHT");
            switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (((boolean) newValue)) {
                        cacheUtils.setBoolean(ISDARK_THEME, true, getContext());
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        cacheUtils.setBoolean(ISDARK_THEME, false, getContext());
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