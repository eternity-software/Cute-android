package ru.etysoft.cute.activities.signin;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;

import org.json.JSONException;

import java.sql.SQLException;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.main.MainActivity;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.utils.Device;
import ru.etysoft.cute.utils.NetworkStateReceiver;
import ru.etysoft.cuteframework.exceptions.NotCachedException;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.account.LoginRequest;
import ru.etysoft.cuteframework.responses.errors.ErrorHandler;

public class SignInPresenter implements SignInContract.Presenter {

    private final SignInContract.View signInView;
    private final SignInContract.Model signInModel;
    private final Activity context;
    private NetworkStateReceiver networkStateReceiver;

    public SignInPresenter(SignInContract.View signInView, Activity context) {
        signInModel = new SignInModel();
        this.signInView = signInView;
        this.context = context;
        initializeNetworkStateHolder();
    }

    @Override
    public void onSignInButtonClick(final String login, final String password) {
        signInView.setEnabledActionButton(false);
        Thread requestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LoginRequest.LoginResponse loginResponse = signInModel.signIn(login, password);

                    if (loginResponse.isSuccess()) {
                        CachedValues.setLogin(context, login);
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    } else {
                        ErrorHandler errorHandler = loginResponse.getErrorHandler();
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                signInView.showError(context.getResources().getString(R.string.err_unknown));
                            }
                        });

                    }
                } catch (ResponseException e) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            signInView.showError(context.getResources().getString(R.string.err_unknown));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            signInView.showError(context.getResources().getString(R.string.err_json));
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        signInView.setEnabledActionButton(true);
                    }
                });
            }
        });
        if(Device.isDeviceRegistered())
        {
            requestThread.start();
        }
        else
        {
            Device.registerDevice(new Device.DeviceCallback() {
                @Override
                public void onRegistered() {
                    requestThread.start();
                }

                @Override
                public void onError() {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            signInView.showError(context.getResources().getString(R.string.err_unknown));
                        }
                    });
                }
            });
        }
    }

    @Override
    public void initializeNetworkStateHolder() {
        Runnable onlineRunnable = new Runnable() {
            @Override
            public void run() {
                signInView.hideError();
            }
        };

        Runnable offlineRunnable = new Runnable() {
            @Override
            public void run() {
                signInView.showError(context.getResources().getString(R.string.err_no_internet));
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
