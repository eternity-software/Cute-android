package ru.etysoft.cute.activities.fragments.account;

import android.app.Activity;

import ru.etysoft.cute.activities.editprofile.EditProfileActivity;
import ru.etysoft.cute.components.CuteToast;
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
                                CachedValues.setStatus(context, getAccountResponse.getStatus());
                                CachedValues.setBio(context, getAccountResponse.getBio());
                                CachedValues.setDisplayName(context, getAccountResponse.getDisplayName());

                                String photoPath = null;
                                try {
                                    photoPath = getAccountResponse.getAvatarPath();
                                    System.out.println(photoPath);
                                } catch (ResponseException ignored) {
                                    ignored.printStackTrace();
                                }
                                CachedValues.setLogin(context, getAccountResponse.getLogin());

                                view.setAccountInfo(getAccountResponse.getLogin(), getAccountResponse.getStatus(), photoPath,
                                        Integer.parseInt(getAccountResponse.getId()));

                            }
                        }
                    });

                } catch (ResponseException e) {
                    e.printStackTrace();
                } catch (NotCachedException e) {
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    CuteToast.showError(e.getMessage(), context);
                }

            }
        });
        thread.start();

    }
}
