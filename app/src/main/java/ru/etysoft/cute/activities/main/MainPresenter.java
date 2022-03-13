package ru.etysoft.cute.activities.main;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import java.sql.SQLException;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.confirmation.ConfirmationActivity;
import ru.etysoft.cute.activities.meet.MeetActivity;
import ru.etysoft.cute.bottomsheets.FloatingBottomSheet;
import ru.etysoft.cute.data.CacheUtils;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.CrashExceptionHandler;
import ru.etysoft.cute.utils.NetworkStateReceiver;
import ru.etysoft.cuteframework.exceptions.NotCachedException;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.account.GetAccountRequest;
import ru.etysoft.cuteframework.storage.Cache;


public class MainPresenter implements MainContract.Presenter {

    private final MainContract.View mainView;
    private final MainContract.Model mainModel;
    private final Activity context;
    private NetworkStateReceiver networkStateReceiver;

    public MainPresenter(MainContract.View mainView, Activity context) {
        this.mainView = mainView;
        this.context = context;
        mainModel = new MainModel();
        startCrashHandler();
        updateAccountData();
    }

    @Override
    public void startCrashHandler() {
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CrashExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CrashExceptionHandler(context));
        }
    }

    @Override
    public void updateAccountData() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                GetAccountRequest getAccount = new GetAccountRequest();
                try {
                    GetAccountRequest.GetAccountResponse getAccountResponse = getAccount.execute();

                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                if (getAccountResponse.isSuccess()) {
                                    boolean isConfirm = getAccountResponse.getAccount().isConfirmed();
                                    boolean isBlocked = getAccountResponse.getAccount().isBlocked();



                                    if (!isConfirm && !isBlocked) {
                                        mainView.startConfirmationActivity();
                                    } else if (isBlocked) {
                                        mainView.showBannedBottomSheet();
                                    }
                                } else {

                                    Cache.clean();
                                    mainView.startMeetActivity();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
        thread.start();
    }

    @Override
    public void onDestroy() {

    }
}
