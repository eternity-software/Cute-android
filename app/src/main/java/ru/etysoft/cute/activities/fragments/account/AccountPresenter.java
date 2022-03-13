package ru.etysoft.cute.activities.fragments.account;

import android.app.Activity;
import android.content.Intent;


import ru.etysoft.cute.activities.ImagePreview;
import ru.etysoft.cute.components.CuteToast;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cuteframework.CuteFramework;
import ru.etysoft.cuteframework.exceptions.NoSuchValueException;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.account.GetAccountRequest;
import ru.etysoft.cuteframework.methods.friend.GetFriends.FriendListRequest;
import ru.etysoft.cuteframework.methods.friend.GetFriends.FriendListResponse;
import ru.etysoft.cuteframework.models.Account;

public class AccountPresenter implements AccountContact.Presenter {
    private final Activity context;
    private final AccountContact.View view;
    private String photoPath = null;

    public AccountPresenter(Activity activity, AccountContact.View view) {
        this.context = activity;
        this.view = view;
    }

    @Override
    public void openAvatar() {
        if (photoPath != null) {
            Intent intent = new Intent(context, ImagePreview.class);
            intent.putExtra("url", photoPath);
            context.startActivity(intent);
        }

    }

    @Override
    public void updateData() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final GetAccountRequest.GetAccountResponse getAccountResponse = new GetAccountRequest().execute();
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getAccountResponse.isSuccess()) {
                                Account account = null;
                                try {
                                    account = getAccountResponse.getAccount();
                                    String status = "Set your status";
                                    try {

                                        status = account.getStatus();
                                    } catch (NoSuchValueException ignored) {

                                    }
                                    view.setAccountInfo(account.getName(), status, null,
                                            account.getId());
                                } catch (NoSuchValueException e) {
                                    e.printStackTrace();
                                }



                            }
                        }
                    });
                    
                } catch (final Exception e) {
                    if(context != null)
                    {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CuteToast.showError(e.getMessage(), context);
                            }
                        });
                    }


                }

            }
        });
        thread.start();
        updateFriends();

    }

    @Override
    public void updateFriends() {

        Thread processServerRequest = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FriendListResponse friendListResponse = (new FriendListRequest(CachedValues.getSessionKey(context))).execute();
                    if (friendListResponse.isSuccess()) {
                        view.getFriends().clear();
                        view.getFriends().addAll(friendListResponse.getFriends());
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                view.updateFriendsViews();
                            }
                        });
                    }

                } catch (Exception ignored) {
                }
            }
        });
        processServerRequest.start();

    }
}
