package ru.etysoft.cute.activities.main;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.confirmation.ConfirmationActivity;
import ru.etysoft.cute.activities.meet.MeetActivity;
import ru.etysoft.cute.bottomsheets.FloatingBottomSheet;
import ru.etysoft.cute.data.CacheUtils;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.CrashExceptionHandler;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.utils.NetworkStateReceiver;
import ru.etysoft.cuteframework.methods.account.GetAccount.GetAccountResponse;

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
        try {

            final String token = CachedValues.getSessionKey(context);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final GetAccountResponse getAccountResponse = mainModel.getAccountInfo(token);

                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    if (getAccountResponse.isSuccess()) {
                                        String confirmStatus = getAccountResponse.getConfirm();

                                        if (confirmStatus.equals("Y")) {
                                            String displayName = getAccountResponse.getDisplayName();
                                            String login = getAccountResponse.getLogin();

                                            CachedValues.setId(context, getAccountResponse.getId());
                                            CachedValues.setDisplayName(context, displayName);
                                            CachedValues.setLogin(context, login);
                                        } else if (confirmStatus.equals("B")) {
                                            final FloatingBottomSheet floatingBottomSheet = new FloatingBottomSheet();

                                            View.OnClickListener onClickListener = new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    Intent intent = new Intent(context, MeetActivity.class);
                                                    context.startActivity(intent);
                                                    floatingBottomSheet.dismiss();

                                                }
                                            };

                                            floatingBottomSheet.setContent(context.getResources().getDrawable(R.drawable.icon_uber),
                                                    context.getString(R.string.banned_title),
                                                    context.getString(R.string.banned_text));
                                            floatingBottomSheet.setCancelable(false);
                                            floatingBottomSheet.show(((MainActivity) context).getSupportFragmentManager(), "blocked");
                                        } else if (confirmStatus.equals("N")) {
                                            Intent intent = new Intent(context, ConfirmationActivity.class);
                                            context.startActivity(intent);
                                            context.finish();
                                        } else {
                                            Intent intent = new Intent(context, MeetActivity.class);
                                            context.startActivity(intent);
                                            context.finish();
                                        }
                                    } else {
                                        Intent intent = new Intent(context, MeetActivity.class);
                                        context.startActivity(intent);
                                        context.finish();
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


        } catch (NotCachedException e) {
            Intent intent = new Intent(context, MeetActivity.class);
            CacheUtils cacheUtils = CacheUtils.getInstance();
            cacheUtils.clean(context);
            context.startActivity(intent);
            context.finish();
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {

    }
}
