package ru.etysoft.cute.activities.signin;

import android.app.Activity;
import android.content.IntentFilter;

import org.json.JSONException;

import ru.etysoft.cute.R;
import ru.etysoft.cute.utils.NetworkStateReceiver;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.Authorization.AuthorizationResponse;
import ru.etysoft.cuteframework.responses.errors.ErrorHandler;

public class SignInPresenter implements SignInContract.Presenter {

    private SignInContract.View signInView;
    private SignInContract.Model signInModel;
    private Activity context;
    private NetworkStateReceiver networkStateReceiver;

    public SignInPresenter(SignInContract.View signInView, Activity context) {
        signInModel = new SignInModel();
        this.signInView = signInView;
        this.context = context;
        initializeNetworkStateHolder();
    }

    @Override
    public void onSignInButtonClick(final String login, final String password) {
        Thread requestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AuthorizationResponse authorizationResponse = signInModel.sign(login, password);
                    if (authorizationResponse.isSuccess()) {
                        String sessionKey = authorizationResponse.getSessionKey();

                    } else {
                        ErrorHandler errorHandler = authorizationResponse.getErrorHandler();
                        signInView.showError(context.getResources().getString(R.string.err_unknown));
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

                }
            }
        });
        requestThread.start();
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
