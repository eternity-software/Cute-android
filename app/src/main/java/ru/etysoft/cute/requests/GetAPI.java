package ru.etysoft.cute.requests;

import android.app.Activity;
import android.content.Intent;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.MainActivity;
import ru.etysoft.cute.activities.Meet;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.utils.CustomToast;
import ru.etysoft.cute.utils.ErrorCodes;
import ru.etysoft.cute.utils.Logger;

public class GetAPI {

    public static void execute(final String url, final APIRunnable afterExecute, final @NotNull Activity activity, final String methodName) {
        try {
            final Thread threadExecute = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        final Request request = new Request.Builder()
                                .url(url)
                                .build();
                        try (final Response response = client.newCall(request).execute()) {
                            final String result = response.body().string();
                            CacheResponse.saveResponseToCache(url, result, new AppSettings(activity));
                            Logger.logResponse(result, methodName);
                            try {
                                final JSONObject jsonObject = new JSONObject(result);
                                boolean isSuccess = false;
                                String code = null;
                                if (jsonObject.getString("type").equals("success")) {
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
                                            afterExecute.setResponse(result);
                                            afterExecute.run();
                                        } catch (Exception e) {
                                           e.printStackTrace();
                                        }
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                                final String finalResult = result;
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        afterExecute.setUrl(url);
                                        afterExecute.setResponse(null);
                                        afterExecute.run();
                                        Logger.logError("JSON error. Error: " + finalResult);
                                    }
                                });
                            }
                        } catch (SocketTimeoutException e) {
                            Logger.logRequest("TIMEOUT", "time");
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    afterExecute.setUrl(url);
                                    afterExecute.setResponse(null);
                                    afterExecute.run();
                                }
                            });
                        }
                    } catch (final Exception e) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                afterExecute.setUrl(url);
                                afterExecute.setResponse(null);
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
            e.printStackTrace();
        }
    }

    public static void executeCache(final String url, final APIRunnable afterExecute, final Activity activity, final String methodName) {
        Thread threadExecute = new Thread(new Runnable() {
            @Override
            public void run() {
                if (CacheResponse.hasCache(url, new AppSettings(activity))) {
                    final String result = CacheResponse.getResponseFromCache(url, new AppSettings(activity));
                    Logger.logResponse(result, methodName);
                    try {
                        final JSONObject jsonObject = new JSONObject(result);
                        boolean isSuccess = false;
                        String code = null;
                        if (jsonObject.getString("type").equals("success")) {
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
                                    if (!finalCode.equals("#CM003.1") && MainActivity.isDev) {
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
                                afterExecute.setUrl(url);
                                afterExecute.setResponse(result);
                                afterExecute.run();
                            }
                        });

                    } catch (JSONException e) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final String finalResult = result;
                                Logger.logError("JSON error. Error: " + finalResult);
                            }
                        });
                    }

                    if (Methods.hasInternet(activity)) {
                        execute(url, afterExecute, activity, methodName);
                    }
                } else {

                    Logger.logActivity("Hasn't cache");
                    if (Methods.hasInternet(activity)) {
                        execute(url, afterExecute, activity, methodName);
                    }
                }
            }


        });
        threadExecute.start();


    }

}

