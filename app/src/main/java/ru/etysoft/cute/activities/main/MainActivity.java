package ru.etysoft.cute.activities.main;

import static android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.confirmation.ConfirmationActivity;
import ru.etysoft.cute.activities.fragments.account.AccountFragment;
import ru.etysoft.cute.activities.fragments.chatslist.ChatsListFragment;
import ru.etysoft.cute.activities.fragments.explore.ExploreFragment;
import ru.etysoft.cute.activities.meet.MeetActivity;
import ru.etysoft.cute.activities.stock;
import ru.etysoft.cute.bottomsheets.FloatingBottomSheet;
import ru.etysoft.cute.media.AudioOutputHandler;
import ru.etysoft.cute.media.MediaActions;
import ru.etysoft.cute.media.MediaService;
import ru.etysoft.cute.services.NotificationService;
import ru.etysoft.cute.themes.Theme;
import ru.etysoft.cute.utils.Permissions;

import ru.etysoft.cute.utils.ViewPagerAdapter;


public class MainActivity extends AppCompatActivity implements FloatingBottomSheet.BottomSheetListener, MainContract.View, LifecycleObserver {

    public static final boolean isDev = false;

    private MainPresenter mainPresenter;

    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private MenuItem menuItem;
    private ChatsListFragment fragmentDialogs;
    private AccountFragment fragmentAccount;
    private ExploreFragment fragmentExplore;
    private BroadcastReceiver broadcastReceiver;
    private final BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainPresenter = new MainPresenter(this, this);

        startService(new Intent(this, NotificationService.class));

        //devOptions();




        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int action = intent.getExtras().getInt("action", 1);
                MediaActions.processAction(action);
                if(MediaService.isStopped)
                {
                    ContextCompat.startForegroundService(
                            MainActivity.this.getApplicationContext(),
                            new Intent(MainActivity.this.getApplicationContext(), MediaService.class));
                }
            }
        };



        registerReceiver(broadcastReceiver, new IntentFilter(MediaActions.BROADCAST_INTENT));

        setupNavigation();
        Permissions.requestAll(this);

//        MediaService.play("8(913)", "Прыгай, за руки держась","https://ru.muzikavsem.org/dl/276846446/8913_-_Prygajj_za_ruki_derzhas_(ru.muzikavsem.org).mp3"
//                , null);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ProcessLifecycleOwner.get().getLifecycle().addObserver(MainActivity.this);
            }
        });
        thread.start();


    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        try {
            //SocketHolder.getChatSocket().sendRequest(0, MemberStateChangedEvent.States.OFFLINE);
        } catch (Exception e) {

        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        try {
            //  SocketHolder.getChatSocket().sendRequest(0, MemberStateChangedEvent.States.ONLINE);
        } catch (Exception e) {

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permissions.requestAll(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        //SocketHolder.clear();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                // SocketHolder.initialize(CachedValues.getSessionKey(MainActivity.this));

            }
        });
        thread.start();

        AudioOutputHandler audioOutputHandler = new AudioOutputHandler();
        registerReceiver(audioOutputHandler, new IntentFilter(ACTION_AUDIO_BECOMING_NOISY));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Deprecated
    public void devOptions() {
        final FloatingBottomSheet floatingBottomSheet = new FloatingBottomSheet();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, stock.class);
                startActivity(intent);
                floatingBottomSheet.dismiss();
            }
        };

        floatingBottomSheet.setContent(getResources().getDrawable(R.drawable.logo), "Режим разработчика", "Вы можете открыть дополнительное меню разработчика.", "ОТКРЫТЬ", null, onClickListener, null);
        floatingBottomSheet.show(getSupportFragmentManager(), "blocked");
        floatingBottomSheet.setCancelable(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void setupNavigation() {
        bottomNavigationView = findViewById(R.id.nav_view);
        viewPager = findViewById(R.id.view);
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
                if (position == 2) {
                    fragmentAccount.getPresenter().updateData();
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                menuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        setupViewPager(viewPager);

        Theme.applyThemeToActivity(this);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.dialogs);

        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_checked}  // pressed
        };
        int[] colors = new int[]{
                Theme.getColor(this, R.color.colorNavigationUnfocused),
                Theme.getColor(this, R.color.colorNavigationFocused),
        };

        navView.setItemIconTintList(new ColorStateList(states, colors));

        navView.setBackgroundColor(Theme.getColor(this, R.color.colorBackground));
        //   navView.setBackground(new ColorDrawable(Theme.getColor(this, R.color.colorBackground)));
//        RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(Color.GRAY), navView.getBackground(), null);
//        navView.setBackground(rippleDrawable);
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        // Выбрать раздел сообщений
        viewPager.setCurrentItem(1);
    }

    @Override
    public void startMeetActivity() {
        Intent intent = new Intent(this, MeetActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void startConfirmationActivity() {
        Intent intent = new Intent(MainActivity.this, ConfirmationActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showBannedBottomSheet() {
        final FloatingBottomSheet floatingBottomSheet = new FloatingBottomSheet();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMeetActivity();
            }
        };
        floatingBottomSheet.setContent(getResources().getDrawable(R.drawable.icon_uber),
                getString(R.string.banned_title),
               getString(R.string.banned_text));
        floatingBottomSheet.setCancelable(false);
        floatingBottomSheet.show((this).getSupportFragmentManager(), "blocked");
    }

    @Override
    public void onBackPressed() {

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragmentExplore = new ExploreFragment();
        fragmentDialogs = new ChatsListFragment();
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
