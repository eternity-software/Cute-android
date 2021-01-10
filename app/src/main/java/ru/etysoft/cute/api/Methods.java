package ru.etysoft.cute.api;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.URLEncoder;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.requests.CacheResponse;
import ru.etysoft.cute.requests.GET;
import ru.etysoft.cute.requests.GetAPI;
import ru.etysoft.cute.requests.PostAPI;
import ru.etysoft.cute.utils.Logger;
import ru.etysoft.cute.utils.StringFormatter;

public class Methods {

    public static String domain = "https://api.mcute.ru/";
    public static String options = "&v=V0001";

    public static void initialize(Context context) {
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

    public static void getAccount(String id, APIRunnable apiRunnable, Activity activity) {
        String finalurl = domain + "users.get?id=" + id + options;
        String methodName = "GETPROFILE";
        Logger.logRequest("GET", "[GETPROFILE]: " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity, methodName);
    }

    public static String longpoolMessages(String session, int ts, Activity activity) {
        String finalurl = domain + "longpoll.getMessages?session=" + session + "&ts=" + ts + options;
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
        String finalurl = domain + "conversation.leave?session=" + session + "&cid=" + cid + options;
        String methodName = "LEAVECONV";
        Logger.logRequest("GET", methodName + ": " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity, methodName);
    }

    public static void deleteConversationLocaly(String session, String cid, APIRunnable apiRunnable, Activity activity) {
        String finalurl = domain + "conversation.clear?session=" + session + "&cid=" + cid + options;
        String methodName = "DELCONVLOCAL";
        Logger.logRequest("GET", methodName + ": " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity, methodName);
    }

    public static void getConversationInfo(String session, String cid, APIRunnable apiRunnable, Activity activity) {
        String finalurl = domain + "conversation.info?session=" + session + "&cid=" + cid + options;
        String methodName = "GETCONVINFO";
        Logger.logRequest("GET", methodName + ": " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity, methodName);
    }


    public static void getConversations(String session, APIRunnable apiRunnable, Activity activity) {
        String finalurl = domain + "account.getConversations?session=" + session + options;
        String methodName = "GETCONVS";
        Logger.logRequest("GET", methodName + ": " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity, methodName);
    }

    public static void getCacheConversations(String session, APIRunnable apiRunnable, Activity activity) {
        String finalurl = domain + "account.getConversations?session=" + session + options;
        String methodName = "GETCONVS(cache)";
        Logger.logRequest("CACHE", methodName + ": " + finalurl);
        GetAPI.executeCache(finalurl, apiRunnable, activity, methodName);
    }

    public static void getMessages(String session, String cid, APIRunnable apiRunnable, Activity activity) {


        String finalurl = domain + "conversation.getMessages?session=" + session + "&cid=" + cid + options;
        String methodName = "GETMESSAGES";
        Logger.logRequest("GET", methodName + ": " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity, methodName);
    }

    public static void sendTextMessage(String session, String message, String cid, APIRunnable apiRunnable, Activity activity) {

        try {
            message = StringFormatter.format(URLEncoder.encode(message, "UTF-8"));
            String finalurl = domain + "conversation.send";
            String params = "session=" + session + "&cid=" + cid + "&text=" + message + options;
            String methodName = "SENDTEXTMESSAGE";
            Logger.logRequest("POST", methodName + ": " + finalurl + "?" + params);
            PostAPI.execute(finalurl, params, apiRunnable, activity, methodName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createConversations(String session, String type, String name, String description, APIRunnable apiRunnable, Activity activity) {

        name = StringFormatter.format(name);
        description = StringFormatter.format(description);

        String finalurl = domain + "conversation.create?session=" + session + "&type=" + type + "&name=" + name + "&description=" + description + options;
        String methodName = "CREATECONV";
        Logger.logRequest("GET", methodName + ": " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity, methodName);
    }

    public static String sendNewConfirmationCode(String session) {
        String finalurl = domain + "account.sendConfirm?session=" + session + options;
        Logger.logRequest("GET", "[SENDEMAIL]: " + finalurl);
        return GET.execute(finalurl);
    }

    public static void getMyAccount(String session, APIRunnable apiRunnable, Activity activity) {
        String finalurl = domain + "account.get?session=" + session + options;
        String methodName = "GETMYACC";
        Logger.logRequest("GET", methodName + ": " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity, methodName);
    }

    public static void changePassword(String session, String password, String newPassword, APIRunnable apiRunnable, Activity activity) {
        password = StringFormatter.format(password);
        newPassword = StringFormatter.format(newPassword);

        String finalurl = domain + "account.changePassword?session=" + session + "&password=" + password + "&new=" + newPassword + options;
        String methodName = "CHANGEPASS";
        Logger.logRequest("GET", methodName + ": " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity, methodName);
    }

    public static void createAccount(String username, String password, String email, APIRunnable apiRunnable, Activity activity) {
        String finalurl = domain + "account.create?nickname=" + username + "&email=" + email + "&password=" + password + options;
        String methodName = "CREATEACC";
        Logger.logRequest("GET", methodName + ": " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity, methodName);
    }

    public static String getErrorCodes(Context context) {
        AppSettings appSettings = new AppSettings(context);
        String finalurl = domain + "system.getErrors?" + options;
        String methodName = "GETERRCODES";
        Logger.logRequest("GET", methodName + ": " + finalurl);

        if (CacheResponse.hasCache(finalurl, appSettings) && false) {
            String cachedResponse = CacheResponse.getResponseFromCache(finalurl, appSettings);
            Logger.logCache(methodName + ": " + cachedResponse);
            return cachedResponse;
        } else {
            String response = GET.execute(finalurl);
            CacheResponse.saveResponseToCache(finalurl, response, appSettings);
            return response;
        }

    }

    public static void closeSession(String session, APIRunnable apiRunnable, Activity activity) {
        String finalurl = domain + "account.logout?session=" + session + options;
        String methodName = "CLOSESESSION";
        Logger.logRequest("GET", methodName + ": " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity, methodName);
    }

    public static boolean hasInternet(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
