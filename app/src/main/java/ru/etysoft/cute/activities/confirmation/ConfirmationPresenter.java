package ru.etysoft.cute.activities.confirmation;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;

import org.json.JSONException;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.main.MainActivity;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.utils.NetworkStateReceiver;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.account.Confirmation.ConfirmationResponse;
import ru.etysoft.cuteframework.responses.errors.ErrorHandler;

public class ConfirmationPresenter implements ConfirmationContract.Presenter {

    private ConfirmationContract.View confirmationView;
    private ConfirmationContract.Model confirmationModel;
    private Activity context;
    private NetworkStateReceiver networkStateReceiver;

    public ConfirmationPresenter(ConfirmationContract.View confirmationView, Activity context) {
        confirmationModel = new ConfirmationModel();
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
                try {
                    ConfirmationResponse registrationResponse = confirmationModel.confirm(Integer.parseInt(code), CachedValues.getSessionKey(context));

                    if (registrationResponse.isSuccess()) {
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    } else {
                        ErrorHandler errorHandler = registrationResponse.getErrorHandler();
                        confirmationView.showError(context.getResources().getString(R.string.err_unknown));
                    }
                } catch (ResponseException e) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            confirmationView.showError(context.getResources().getString(R.string.err_unknown));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            confirmationView.showError(context.getResources().getString(R.string.err_json));
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            confirmationView.showError(context.getResources().getString(R.string.err_unknown));
                        }
                    });
                }
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        confirmationView.setEnabledActionButton(true);
                    }
                });
            }
        });
        requestThread.start();
    }

    @Override
    public void onSendNewConfirmCodeButtonClick() {

    }

    @Override
    public void onCantReceiveButtonClick() {

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

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkStateReceiver = new NetworkStateReceiver(onlineRunnable, offlineRunnable);
        context.registerReceiver(networkStateReceiver, filter);
    }

    @Override
    public void onDestroy() {
        context.unregisterReceiver(networkStateReceiver);
    }
}
