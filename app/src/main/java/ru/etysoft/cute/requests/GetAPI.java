package ru.etysoft.cute.requests;

import android.app.Activity;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.MainActivity;
import ru.etysoft.cute.activities.Meet;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.utils.CustomToast;
import ru.etysoft.cute.utils.ErrorCodes;
import ru.etysoft.cute.utils.Logger;

public class GetAPI {

    public static void execute(final String url, final APIRunnable afterExecute, final Activity activity) {
        Thread threadExecute = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    final Request request = new Request.Builder()
                            .url(url)
                            .build();
                    try (final Response response = client.newCall(request).execute()) {
                        final String result = response.body().string();
                        Logger.logResponse(result);
                        try {
                            final JSONObject jsonObject = new JSONObject(result);
                            boolean isSuccess = false;
                            if (jsonObject.getString("type").equals("success")) {
                                isSuccess = true;
                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String code = jsonObject.getString("code");

                                            if (code.equals("#AM003.2") | code.equals("#AM003.1")) {
                                                Intent intent = new Intent(activity, Meet.class);
                                                AppSettings appSettings = new AppSettings(activity);
                                                appSettings.clean();
                                                activity.startActivity(intent);
                                                activity.finish();
                                            }
                                            if (!code.equals("#CM003.1") && MainActivity.isDev) {
                                                CustomToast.show(ErrorCodes.getError(code), R.drawable.icon_error, activity);
                                            }

                                        } catch (JSONException e) {
                                            CustomToast.show(activity.getString(R.string.err_json), R.drawable.icon_error, activity);
                                        }
                                    }
                                });
                            }
                            final boolean finalIsSuccess = isSuccess;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    afterExecute.url = url;
                                    afterExecute.isSuccess = finalIsSuccess;
                                    afterExecute.setResponse(result);
                                    afterExecute.run();
                                }
                            });

                        } catch (JSONException e) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomToast.show(activity.getString(R.string.err_json), R.drawable.icon_error, activity);
                                }
                            });
                        }
                    }

                } catch (final Exception e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomToast.show(activity.getString(R.string.err_no_internet), R.drawable.icon_error, activity);
                            e.printStackTrace();
                        }
                    });

                }
            }
        });
        threadExecute.start();
    }

    public static void executeCache(final String url, final APIRunnable afterExecute, final Activity activity) {
        Thread threadExecute = new Thread(new Runnable() {
            @Override
            public void run() {
                if (CacheResponse.hasCache(url, new AppSettings(activity))) {
                    final String result = CacheResponse.getResponseFromCache(url, new AppSettings(activity));
                    Logger.logResponse(result);
                    try {
                        final JSONObject jsonObject = new JSONObject(result);
                        boolean isSuccess = false;
                        if (jsonObject.getString("type").equals("success")) {
                            isSuccess = true;
                        } else {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String code = jsonObject.getString("code");

                                        if (code.equals("#AM003.2") | code.equals("#AM003.1")) {
                                            Intent intent = new Intent(activity, Meet.class);
                                            AppSettings appSettings = new AppSettings(activity);
                                            appSettings.clean();
                                            activity.startActivity(intent);
                                            activity.finish();
                                        }
                                        if (!code.equals("#CM003.1") && MainActivity.isDev) {
                                            CustomToast.show(ErrorCodes.getError(code), R.drawable.icon_error, activity);
                                        }

                                    } catch (JSONException e) {
                                        CustomToast.show(activity.getString(R.string.err_json), R.drawable.icon_error, activity);
                                    }
                                }
                            });
                        }
                        final boolean finalIsSuccess = isSuccess;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                afterExecute.url = url;
                                afterExecute.isSuccess = finalIsSuccess;
                                afterExecute.setResponse(result);
                                afterExecute.run();
                            }
                        });

                    } catch (JSONException e) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CustomToast.show(activity.getString(R.string.err_json), R.drawable.icon_error, activity);
                            }
                        });
                    }

                } else {
                    Logger.logActivity("Hasn't cache");
                    if (Methods.hasInternet(activity)) {
                        execute(url, afterExecute, activity);
                    }
                }
            }


        });
        threadExecute.start();


    }

}

