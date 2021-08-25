package ru.etysoft.cute.activities.fragments.account;

import android.app.Activity;

import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cuteframework.Methods;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.account.GetAccount.GetAccountResponse;

public class AccountPresenter implements AccountContact.Presenter{
    private Activity context;
    private AccountContact.View view;

    public AccountPresenter(Activity activity, AccountContact.View view){
        this.context = activity;
        this.view = view;
    }

    @Override
    public void updateData() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final GetAccountResponse getAccountResponse = Methods.getInfo(CachedValues.getSessionKey(context));
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getAccountResponse.isSuccess()) {
                                view.setAccountInfo(getAccountResponse.getLogin(), "status", "photo",
                                        Integer.parseInt(getAccountResponse.getId()));
                            }
                        }
                    });

                } catch (ResponseException e) {
                    e.printStackTrace();
                } catch (NotCachedException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();

    }
}
