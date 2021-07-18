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
import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.BuildConfig;
import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.Meet.MeetActivity;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.api.response.ResponseHandler;
import ru.etysoft.cute.utils.CustomToast;
import ru.etysoft.cute.utils.ErrorCodes;

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
        if (appSettings.hasKey(ISDARK_THEME)) {
            if (appSettings.getBoolean(ISDARK_THEME)) {
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
                    final Runnable toRun = new Runnable() {
                        @Override
                        public void run() {
                            AppSettings appSettings = new AppSettings(getActivity());

                            APIRunnable apiRunnable = new APIRunnable() {
                                @Override
                                public void run() {
                                    super.run();
                                    try {
                                        ResponseHandler responseHandler = new ResponseHandler(getResponse());
                                        if (!responseHandler.isSuccess()) {
                                            CustomToast.show(ErrorCodes.getError(responseHandler.getErrorHandler().getErrorCode()), R.drawable.icon_error, getActivity());
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            Methods.closeSession(appSettings.getString("session"), apiRunnable, getActivity());
                            appSettings.clean();
                            Intent meet = new Intent(getActivity(), MeetActivity.class);
                            getActivity().startActivity(meet);
                        }
                    };

                    Runnable cancel = new Runnable() {
                        @Override
                        public void run() {

                        }
                    };
                    AlertDialog cdd = new AlertDialog(getActivity(), getResources().getString(R.string.logout_title), getString(R.string.logout_text), toRun, cancel);
                    cdd.show();
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