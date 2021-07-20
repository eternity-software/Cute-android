package ru.etysoft.cute.activities.signup;

import android.app.Activity;
import android.content.IntentFilter;

import org.json.JSONException;

import ru.etysoft.cute.R;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.utils.NetworkStateReceiver;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.Registration.RegistrationResponse;
import ru.etysoft.cuteframework.responses.errors.ErrorHandler;

public class SignUpPresenter implements SignUpContract.Presenter {
    private SignUpContract.View signUpView;
    private SignUpContract.Model signUpModel;
    private Activity context;
    private NetworkStateReceiver networkStateReceiver;

    public SignUpPresenter(SignUpContract.View signInView, Activity context) {
        signUpModel = new SignUpModel();
        this.signUpView = signInView;
        this.context = context;
        initializeNetworkStateHolder();
    }

    @Override
    public void onSignUpButtonClick(final String login, final String email, final String password) {
        if (signUpView.isPasswordsCorrect()) {
            signUpView.showError(context.getResources().getString(R.string.password_incorrect));
            return;
        }
        signUpView.setEnabledActionButton(false);
        Thread requestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RegistrationResponse registrationResponse = signUpModel.signUp(login, email, password);

                    if (registrationResponse.isSuccess()) {
                        String sessionKey = registrationResponse.getSessionKey();
                        CachedValues.setSessionKey(context, sessionKey);

                    } else {
                        ErrorHandler errorHandler = registrationResponse.getErrorHandler();
                        signUpView.showError(context.getResources().getString(R.string.err_unknown));
                    }
                } catch (ResponseException e) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            signUpView.showError(context.getResources().getString(R.string.err_unknown));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            signUpView.showError(context.getResources().getString(R.string.err_json));
                        }
                    });

                }
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        signUpView.setEnabledActionButton(true);
                    }
                });
            }
        });
        requestThread.start();
    }

    @Override
    public void initializeNetworkStateHolder() {
        Runnable onlineRunnable = new Runnable() {
            @Override
            public void run() {
                signUpView.hideError();
            }
        };

        Runnable offlineRunnable = new Runnable() {
            @Override
            public void run() {
                signUpView.showError(context.getResources().getString(R.string.err_no_internet));
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
