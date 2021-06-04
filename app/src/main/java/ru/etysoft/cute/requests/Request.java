package ru.etysoft.cute.requests;

import android.app.Activity;

import java.util.HashMap;

import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.utils.Logger;

public class Request {

    private String method;
    private HashMap<String, String> params;
    private Activity activity;
    private APIRunnable apiRunnable;

    public Request(String method, HashMap<String, String> params, APIRunnable apiRunnable, Activity activity) {
        this.method = method;
        this.params = params;
        this.activity = activity;
        this.apiRunnable = apiRunnable;
    }

    public void process() {
        String args = "?";

        for (int i = 0; i < params.keySet().size(); i++) {
            String key = (String) params.keySet().toArray()[i];
            String arg = key + "=" + params.get(key);
            args += arg;

            if (i != params.size() - 1) {
                args += "&";
            }
        }

        String finalURL = Methods.domain + method + args + "&" + Methods.options;
        Logger.logRequest("GET", "[" + method + "]: " + finalURL);
        GetAPI.execute(finalURL, apiRunnable, activity, method);
    }

}
