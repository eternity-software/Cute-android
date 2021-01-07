package ru.etysoft.cute.requests;

import android.app.Activity;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.MainActivity;
import ru.etysoft.cute.activities.Meet;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.utils.CustomToast;
import ru.etysoft.cute.utils.ErrorCodes;
import ru.etysoft.cute.utils.Logger;

public class PostAPI {
    public static void execute(final String url, final String params, final APIRunnable afterExecute, final Activity activity, final String methodName) {
        try {
            final Thread threadExecute = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        final String result = POST.executeFromData(url, params);
                        Logger.logResponse(result, methodName);
                        try {
                            final JSONObject jsonObject = new JSONObject(result);
                            boolean isSuccess = false;
                            String code = null;
                            if (jsonObject.getString("status").equals("success")) {
                                isSuccess = true;
                            } else {
                                code = jsonObject.getString("code");

                                final String finalCode = code;
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (finalCode.equals("#AM003.2") | finalCode.equals("#AM003.1")) {
                                            Intent intent = new Intent(activity, Meet.class);
                                            AppSettings appSettings = new AppSettings(activity);
                                            appSettings.clean();
                                            activity.startActivity(intent);
                                            activity.finish();
                                        }
                                        if (!finalCode.equals("#CM003.1") && MainActivity.isDev && !finalCode.equals("#CMM004.1")) {
                                            CustomToast.show(ErrorCodes.getError(finalCode), R.drawable.icon_error, activity);
                                        }
                                    }
                                });
                            }
                            final boolean finalIsSuccess = isSuccess;

                            final String finalCode1 = code;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        afterExecute.setUrl(url);
                                        afterExecute.setSuccess(finalIsSuccess);
                                        afterExecute.setResponse(result);
                                        afterExecute.setErrorCode(finalCode1);
                                        afterExecute.run();
                                    } catch (Exception e) {

                                    }
                                }
                            });

                        } catch (JSONException e) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    afterExecute.setUrl(url);
                                    afterExecute.setSuccess(false);
                                    afterExecute.setResponse(null);
                                    afterExecute.setErrorCode(null);
                                    afterExecute.run();
                                    CustomToast.show(activity.getString(R.string.err_json), R.drawable.icon_error, activity);
                                }
                            });
                        }
                    } catch (final Exception e) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                afterExecute.setUrl(url);
                                afterExecute.setSuccess(false);
                                afterExecute.setResponse(null);
                                afterExecute.setErrorCode(null);
                                afterExecute.run();
                                e.printStackTrace();
                            }
                        });
                    }
                    return;
                }

            });
            threadExecute.start();
        } catch (Exception e) {

        }
    }
}
