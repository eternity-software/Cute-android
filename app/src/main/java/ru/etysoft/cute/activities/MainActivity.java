package ru.etysoft.cute.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.bottomsheets.FloatingBottomSheet;
import ru.etysoft.cute.fragments.account.AccountFragment;
import ru.etysoft.cute.fragments.dialogs.DialogsFragment;
import ru.etysoft.cute.fragments.explore.ExploreFragment;
import ru.etysoft.cute.utils.CustomToast;
import ru.etysoft.cute.utils.ErrorCodes;
import ru.etysoft.cute.utils.Logger;
import ru.etysoft.cute.utils.NetworkStateReceiver;
import ru.etysoft.cute.utils.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity implements FloatingBottomSheet.BottomSheetListener {

    public static final boolean isDev = true;

    private final NetworkStateReceiver stateReceiver = new NetworkStateReceiver();
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private MenuItem menuItem;
    private DialogsFragment fragmentDialogs;
    private AccountFragment fragmentAccount;
    private ExploreFragment fragmentExplore;
    private AppSettings appSettings;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            if (item.getItemId() == R.id.explore) {
                viewPager.setCurrentItem(0);
            } else if (item.getItemId() == R.id.dialogs) {
                viewPager.setCurrentItem(1);
            } else if (item.getItemId() == R.id.account) {
                viewPager.setCurrentItem(2);
            }

            return false;
        }
    };


    private boolean isbanned = false;
    public static boolean amIcreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.logActivity("Created Main");
        appSettings = new AppSettings(this);

        // Проверка сессии
        if (appSettings.getString("session") == null) {
            Intent intent = new Intent(MainActivity.this, Meet.class);
            AppSettings appSettings = new AppSettings(this);
            appSettings.clean();
            startActivity(intent);
            finish();
            return;
        }

        // Инициализация кодов ошибок
        ErrorCodes.initialize(this);

        // Инициализация навигации
        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(stateReceiver, filter);
        stateReceiver.runnable = new Runnable() {
            @Override
            public void run() {
                checksAPI();
                try {
                    fragmentAccount.updateData();
                    fragmentDialogs.updateDialogList();
                } catch (Exception e) {

                }

            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(stateReceiver);
    }

    public void checksAPI() {
        checkAccount();
    }

    public void checkAccount() {
        final String session = appSettings.getString("session");
        if (session != null) {
            final APIRunnable apiRunnable = new APIRunnable() {


                @Override
                public void run() {
                    if (isSuccess()) {
                        try {
                            JSONObject jsonObject = new JSONObject(getResponse());
                            JSONObject data = jsonObject.getJSONObject("data");
                            if (data.getString("confirm").equals("N")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(MainActivity.this, Confirmation.class);
                                        startActivity(intent);
                                        finish();
                                        CustomToast.show(getString(R.string.err_confirm), R.drawable.icon_error, MainActivity.this);
                                    }
                                });
                            } else if (data.getString("blocked").equals("Y")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isbanned) {
                                            final FloatingBottomSheet floatingBottomSheet = new FloatingBottomSheet();

                                            View.OnClickListener onClickListener = new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(MainActivity.this, Meet.class);
                                                    startActivity(intent);
                                                    floatingBottomSheet.dismiss();
                                                }
                                            };
                                            floatingBottomSheet.setContent(getResources().getDrawable(R.drawable.icon_uber), getString(R.string.banned_title), getString(R.string.banned_text));
                                            floatingBottomSheet.show(getSupportFragmentManager(), "blocked");
                                            floatingBottomSheet.setCancelable(false);
                                            isbanned = true;
                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomToast.show(getString(R.string.err_json), R.drawable.icon_error, MainActivity.this);
                                }
                            });
                        }

                    }
                }
            };
            Methods.getMyAccount(session, apiRunnable, MainActivity.this);
        } else {
            if (!isbanned) {
                Intent intent = new Intent(MainActivity.this, Meet.class);
                AppSettings appSettings = new AppSettings(this);
                appSettings.clean();
                startActivity(intent);
                CustomToast.show("Session error", R.drawable.icon_error, MainActivity.this);
                finish();
            }
        }
    }

    public void devOptions() {
        final FloatingBottomSheet floatingBottomSheet = new FloatingBottomSheet();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                floatingBottomSheet.dismiss();
            }
        };

        floatingBottomSheet.setContent(getResources().getDrawable(R.drawable.logo), "Режим разработчика", "Вы можете открыть дополнительное меню разработчика.", "ОТКРЫТЬ", null, onClickListener, null);
        floatingBottomSheet.show(getSupportFragmentManager(), "blocked");
        floatingBottomSheet.setCancelable(true);

    }


    public void setupNavigation() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_view);
        viewPager = (ViewPager) findViewById(R.id.view);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                menuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        setupViewPager(viewPager);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.dialogs);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Выбрать диалоги
        viewPager.setCurrentItem(1);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragmentExplore = new ExploreFragment();
        fragmentDialogs = new DialogsFragment();
        fragmentAccount = new AccountFragment();
        viewPagerAdapter.addFragment(fragmentExplore);
        viewPagerAdapter.addFragment(fragmentDialogs);
        viewPagerAdapter.addFragment(fragmentAccount);
        viewPager.setSaveEnabled(true);
        viewPager.setSaveFromParentEnabled(true);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(viewPagerAdapter);
    }
}
