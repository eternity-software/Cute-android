package ru.etysoft.cute.api;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.URLEncoder;
import java.util.HashMap;

import ru.etysoft.cute.R;
import ru.etysoft.cute.requests.GET;
import ru.etysoft.cute.requests.GetAPI;
import ru.etysoft.cute.requests.PostAPI;
import ru.etysoft.cute.requests.Request;
import ru.etysoft.cute.utils.Logger;
import ru.etysoft.cute.utils.StringFormatter;

public class Methods {

    public static String domain = "https://api.mcute.ru/";
    public static String mainDomain = "https://mcute.ru/";
    public static String options = "&v=V0001";
    public static Context context;

    public static void initialize(Context context) {
        Methods.context = context;
        options = "&v=" + context.getResources().getString(R.string.api_v);
    }


    public static void login(String username, String password, Activity activity, APIRunnable apiRunnable) {
        username = StringFormatter.format(username);
        password = StringFormatter.format(password);

        String finalurl = domain + "account.auth?login=" + username + "&password=" + password + options;
        String methodName = "LOGIN";
        Logger.logRequest("GET", methodName + ": " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity, methodName);
    }

    public static void editChat(HashMap<String, String> params, APIRunnable apiRunnable, Activity activity) {

        Request request = new Request("conversation.edit", params, apiRunnable, activity);
        request.processCache();
    }

    public static void editProfile(HashMap<String, String> params, APIRunnable apiRunnable, Activity activity) {
        Request request = new Request("account.edit", params, apiRunnable, activity);
        request.processCache();
    }

    public static void getAccount(String id, String session, APIRunnable apiRunnable, Activity activity) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("session", session);
        params.put("id", id);
        Request request = new Request("users.get", params, apiRunnable, activity);
        request.processCache();
    }

    public static void joinChat(String session, String cid, APIRunnable apiRunnable, Activity activity) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("session", session);
        params.put("cid", cid);
        Request request = new Request("conversation.join", params, apiRunnable, activity);
        request.processCache();
    }

    public static String getPhotoUrl(String resp) {
        return mainDomain + resp;
    }

    public static void searchChat(String session, String query, APIRunnable apiRunnable, Activity activity) {
        try {
            query = StringFormatter.format(URLEncoder.encode(query, "UTF-8"));

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("session", session);
            params.put("query", query);
            Request request = new Request("conversation.search", params, apiRunnable, activity);
            request.processCache();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String longpoolNotifications(String session, int ts) {
        String finalurl = domain + "longpoll.getNotifications?session=" + session + "&ts=" + ts + options;
        String methodName = "LONGPOLLNOTIFICATIONS";
        Logger.logRequest("GET", "[LONGPOLLNOTIFICATIONS]: " + finalurl);
        return GET.executeNoTimeout(finalurl);
    }

    public static String longpoolNotifications(String session) {
        String finalurl = domain + "longpoll.getNotifications?session=" + session + options;
        String methodName = "LONGPOLLNOTIFICATIONSGETTS";
        Logger.logRequest("GET", "[" + methodName + "]: " + finalurl);
        return GET.executeNoTimeout(finalurl);
    }


    public static String longpoolMessages(String session, int ts, String cid, Activity activity) {
        String finalurl = domain + "longpoll.getMessages?session=" + session + "&ts=" + ts + "&cid=" + cid + options;
        String methodName = "LONGPOLLMESSAGES";
        Logger.logRequest("GET", "[LONGPOLLMESSAGES]: " + finalurl);
        return GET.executeNoTimeout(finalurl);
    }


    public static String sendConfirmationCode(String session, String code) {
        code = StringFormatter.format(code);

        String finalurl = domain + "account.confirm?session=" + session + "&code=" + code + options;
        Logger.logRequest("GET", "[CONFIRM]: " + finalurl);
        return GET.execute(finalurl);
    }

    public static void leaveConversation(String session, String cid, APIRunnable apiRunnable, Activity activity) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("session", session);
        params.put("cid", cid);
        Request request = new Request("conversation.leave", params, apiRunnable, activity);
        request.process();
    }

    public static void deleteConversationLocally(String session, String cid, APIRunnable apiRunnable, Activity activity) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("session", session);
        params.put("cid", cid);
        Request request = new Request("conversation.clear", params, apiRunnable, activity);
        request.process();
    }

    public static void getConversationInfo(String session, String cid, APIRunnable apiRunnable, Activity activity) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("session", session);
        params.put("cid", cid);
        Request request = new Request("conversation.info", params, apiRunnable, activity);
        request.process();
    }


    public static void getConversations(String session, APIRunnable apiRunnable, Activity activity) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("session", session);
        Request request = new Request("account.getConversations", params, apiRunnable, activity);
        request.process();
    }

    public static void getCacheConversations(String session, APIRunnable apiRunnable, Activity activity) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("session", session);
        Request request = new Request("account.getConversations", params, apiRunnable, activity);
        request.processCache();
    }

    public static void getMessages(String session, String cid, APIRunnable apiRunnable, Activity activity) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("session", session);
        params.put("cid", cid);
        Request request = new Request("conversation.getMessages", params, apiRunnable, activity);
        request.process();
    }

    public static void sendTextMessage(String session, String message, String cid, APIRunnable apiRunnable, Activity activity) {

        try {
            message = StringFormatter.format(URLEncoder.encode(message, "UTF-8"));
            String finalurl = domain + "conversation.send";
            String params = "session=" + session + "&cid=" + cid + "&text=" + message + options;
            String methodName = "SENDTEXTMESSAGE";
            Logger.logRequest("POST", methodName + ": " + finalurl + "?" + params);
            PostAPI.executeGET(finalurl, params, apiRunnable, activity, methodName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createConversations(String session, String type, String name, String description, APIRunnable apiRunnable, Activity activity) {
        name = StringFormatter.format(name);
        description = StringFormatter.format(description);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("session", session);
        params.put("type", type);
        params.put("name", name);
        params.put("description", description);
        Request request = new Request("conversation.create", params, apiRunnable, activity);
        request.process();
    }

    public static String sendNewConfirmationCode(String session) {
        String finalurl = domain + "account.sendConfirm?session=" + session + options;
        Logger.logRequest("GET", "[SENDEMAIL]: " + finalurl);
        return GET.execute(finalurl);
    }

    public static void getMyAccount(String session, APIRunnable apiRunnable, Activity activity) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("session", session);
        Request request = new Request("account.get", params, apiRunnable, activity);
        request.process();
    }

    public static void changePassword(String session, String password, String newPassword, APIRunnable apiRunnable, Activity activity) {
        password = StringFormatter.format(password);
        newPassword = StringFormatter.format(newPassword);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("session", session);
        params.put("password", password);
        params.put("new", newPassword);
        Request request = new Request("account.changePassword", params, apiRunnable, activity);
        request.process();
    }

    public static void createAccount(String username, String password, String email, APIRunnable apiRunnable, Activity activity) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("login", username);
        params.put("email", email);
        params.put("password", password);
        Request request = new Request("account.create", params, apiRunnable, activity);
        request.process();
    }

    public static String getErrorCodes(Context context) {
//        AppSettings appSettings = new AppSettings(context);
//        String finalurl = domain + "system.getErrors?" + options;
//        String methodName = "GETERRCODES";
//        Logger.logRequest("GET", methodName + ": " + finalurl);
//
//        if (CacheResponse.hasCache(finalurl, appSettings) && false) {
//            String cachedResponse = CacheResponse.getResponseFromCache(finalurl, appSettings);
//            Logger.logCache(methodName + ": " + cachedResponse);
//            return cachedResponse;
//        } else {
//            String response = GET.execute(finalurl);
//            CacheResponse.saveResponseToCache(finalurl, response, appSettings);
//            return response;
//        }
        return null;
    }

    public static void closeSession(String session, APIRunnable apiRunnable, Activity activity) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("session", session);
        Request request = new Request("account.logout", params, apiRunnable, activity);
        request.process();
    }

    public static boolean hasInternet(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
