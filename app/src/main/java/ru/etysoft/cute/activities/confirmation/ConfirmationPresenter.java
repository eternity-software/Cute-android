package ru.etysoft.cute.activities.confirmation;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;

import org.json.JSONException;

import java.sql.SQLException;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.main.MainActivity;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.utils.NetworkStateReceiver;
import ru.etysoft.cuteframework.exceptions.ResponseException;

import ru.etysoft.cuteframework.methods.BlankResponse;
import ru.etysoft.cuteframework.methods.account.ConfirmRequest;
import ru.etysoft.cuteframework.methods.account.ResendCodeRequest;
import ru.etysoft.cuteframework.responses.errors.ErrorHandler;

public class ConfirmationPresenter implements ConfirmationContract.Presenter {

    private final ConfirmationContract.View confirmationView;
    private final Activity context;
    private NetworkStateReceiver networkStateReceiver;

    public ConfirmationPresenter(ConfirmationContract.View confirmationView, Activity context) {

        this.confirmationView = confirmationView;
        this.context = context;

        try {
            confirmationView.setEmail(CachedValues.getEmail(context));
        } catch (NotCachedException e) {
            confirmationView.showError(context.getResources().getString(R.string.err_email));
        }

        initializeNetworkStateHolder();
    }

    @Override
    public void onConfirmButtonClick(final String code) {
        if (code.length() != 6) {
            confirmationView.showError(context.getResources().getString(R.string.err_confirm_code_length));
            return;
        }
        confirmationView.setEnabledActionButton(false);
        Thread requestThread = new Thread(new Runnable() {
            @Override
            public void run() {

                ConfirmRequest confirmRequest = new ConfirmRequest(code);

                try {
                    if(confirmRequest.execute().isSuccess())
                    {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                confirmationView.showMainActivity();
                            }
                        });
                    }
                    else
                    {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                confirmationView.setEnabledActionButton(true);
                                confirmationView.showError(context.getResources().getString(R.string.err_confirm_code_incorrect));
                            }
                        });
                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }

            }
        });
        requestThread.start();
    }

    @Override
    public void onSendNewConfirmCodeButtonClick() {

    }

    @Override
    public void onCantReceiveButtonClick() {
        Thread request = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BlankResponse blankResponse = new ResendCodeRequest().execute();
                    if(!blankResponse.isSuccess())
                    {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                confirmationView.setEnabledActionButton(true);
                                confirmationView.showError(context.getResources().getString(R.string.err_confirm_code_incorrect));
                            }
                        });
                    }
                    else
                    {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                confirmationView.hideError();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            confirmationView.showError(context.getResources().getString(R.string.err_unknown));
                        }
                    });
                }
            }
        });
        request.start();
    }

    @Override
    public void initializeNetworkStateHolder() {
        Runnable onlineRunnable = new Runnable() {
            @Override
            public void run() {
                confirmationView.hideError();
            }
        };

        Runnable offlineRunnable = new Runnable() {
            @Override
            public void run() {
                confirmationView.showError(context.getResources().getString(R.string.err_no_internet));
            }
        };


        networkStateReceiver = new NetworkStateReceiver(onlineRunnable, offlineRunnable);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(networkStateReceiver, filter);
    }

    @Override
    public void onDestroy() {
        context.unregisterReceiver(networkStateReceiver);
    }
}
