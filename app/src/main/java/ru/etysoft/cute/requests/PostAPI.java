package ru.etysoft.cute.requests;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.etysoft.cute.R;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.requests.attachements.ImageFile;
import ru.etysoft.cute.utils.CustomToast;
import ru.etysoft.cute.utils.Logger;

public class PostAPI {

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    public static void execute(final String url, final HashMap<String, Object> params, final APIRunnable afterExecute, final Activity activity, final String methodName) {
        try {
            final Thread threadExecute = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        String finalurl = url + ":\nPARAMS:";

                        String result = null;
                        MediaType JSON = MediaType.get("application/json; charset=utf-8");
                        OkHttpClient client = new OkHttpClient();
                        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM);

                        Set<String> keys = params.keySet();
                        int imageIndex = 1;
                        for (String key : keys) {
                            Object data = params.get(key);
                            if (data instanceof String) {
                                requestBodyBuilder.addFormDataPart(key, (String) data);
                                finalurl = finalurl + "\n" + key + ": " + data;
                            } else if (data instanceof ImageFile) {
                                requestBodyBuilder.addFormDataPart(key, "image" + imageIndex + ".png",
                                        RequestBody.create(MEDIA_TYPE_PNG, (ImageFile) data));
                                imageIndex++;
                                finalurl = finalurl + "\n" + key + ": " + ((ImageFile) data).getAbsolutePath();
                            }

                        }
                        Logger.logRequest("POST", "[" + methodName + "]: " + finalurl);


                        Request request = new Request.Builder()
                                .url(url)
                                .post(requestBodyBuilder.build())
                                .build();

                        try (Response response = client.newCall(request).execute()) {
                            if (!response.isSuccessful())
                                throw new IOException("Unexpected code " + response);
                            result = response.body().string();
                        }

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

                            }
                            final boolean finalIsSuccess = isSuccess;

                            final String finalCode1 = code;
                            final String finalResult = result;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        afterExecute.setUrl(url);
                                        afterExecute.setSuccess(finalIsSuccess);
                                        afterExecute.setResponse(finalResult);
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

    // TODO: remove
    public static void executeGET(final String url, final String params, final APIRunnable afterExecute, final Activity activity, final String methodName) {
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
