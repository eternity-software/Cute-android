package ru.etysoft.cute.api;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ru.etysoft.cute.requests.APIRunnable;
import ru.etysoft.cute.requests.GET;
import ru.etysoft.cute.requests.GetAPI;
import ru.etysoft.cute.utils.Logger;

public class Methods {

    public static String domain = "https://api.mcute.ru/";

    public static void login(String username, String password, Activity activity, APIRunnable apiRunnable) {
        String finalurl = domain + "account.auth?login=" + username + "&password=" + password;
        Logger.logRequest("GET", "[LOGIN]: " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity);
    }

    public static String isConfirmed(String session, String code) {
        String finalurl = domain + "account.confirm?session=" + session + "&code=" + code;
        Logger.logRequest("GET", "[CONFIRM]: " + finalurl);
        return GET.execute(finalurl);
    }

    public static void getConversations(String session, APIRunnable apiRunnable, Activity activity) {
        String finalurl = domain + "conversation.getList?session=" + session;
        Logger.logRequest("GET", "[GETCONVS]: " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity);
    }

    public static void getCacheConversations(String session, APIRunnable apiRunnable, Activity activity) {
        String finalurl = domain + "conversation.getList?session=" + session;
        Logger.logRequest("CACHE", "[GETCONVS]: " + finalurl);
        GetAPI.executeCache(finalurl, apiRunnable, activity);
    }

    public static String sendNewConfirmationCode(String session) {
        String finalurl = domain + "account.sendConfirmMail?session=" + session;
        Logger.logRequest("GET", "[SENDEMAIL]: " + finalurl);
        return GET.execute(finalurl);
    }

    public static void getMyAccount(String session, APIRunnable apiRunnable, Activity activity) {
        String finalurl = domain + "account.get?session=" + session;
        Logger.logRequest("GET", "[GETMYACCOUNT]: " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity);
    }

    public static void changePassword(String session, String password, String newPassword, APIRunnable apiRunnable, Activity activity) {
        String finalurl = domain + "account.changePassword?session=" + session + "&password=" + password + "&new=" + newPassword;
        Logger.logRequest("GET", "[CHANGEPASS]: " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity);
    }

    public static void createAccount(String username, String password, String email, APIRunnable apiRunnable, Activity activity) {
        String finalurl = domain + "account.create?login=" + username + "&email=" + email + "&passwordConfirm=" + password + "&password=" + password;
        Logger.logRequest("GET", "[CREATE]: " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity);
    }

    public static boolean hasInternet(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
