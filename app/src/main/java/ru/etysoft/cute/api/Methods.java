package ru.etysoft.cute.api;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ru.etysoft.cute.requests.GET;
import ru.etysoft.cute.requests.GetAPI;
import ru.etysoft.cute.utils.Logger;
import ru.etysoft.cute.utils.StringFormatter;

public class Methods {

    public static String domain = "https://api.mcute.ru/";

    public static void login(String username, String password, Activity activity, APIRunnable apiRunnable) {
        username = StringFormatter.format(username);
        password = StringFormatter.format(password);

        String finalurl = domain + "account.auth?login=" + username + "&password=" + password;
        String methodName = "LOGIN";
        Logger.logRequest("GET", methodName + ": " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity, methodName);
    }

    public static String isConfirmed(String session, String code) {
        code = StringFormatter.format(code);

        String finalurl = domain + "account.confirm?session=" + session + "&code=" + code;
        Logger.logRequest("GET", "[CONFIRM]: " + finalurl);
        return GET.execute(finalurl);
    }

    public static void leaveConversation(String session, String cid, APIRunnable apiRunnable, Activity activity) {
        String finalurl = domain + "conversationmember.leave?session=" + session + "&cid=" + cid;
        String methodName = "LEAVECONV";
        Logger.logRequest("GET", methodName + ": " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity, methodName);
    }


    public static void getConversations(String session, APIRunnable apiRunnable, Activity activity) {
        String finalurl = domain + "conversation.getList?session=" + session;
        String methodName = "GETCONVS";
        Logger.logRequest("GET", methodName + ": " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity, methodName);
    }

    public static void getCacheConversations(String session, APIRunnable apiRunnable, Activity activity) {
        String finalurl = domain + "conversation.getList?session=" + session;
        String methodName = "GETCONVS(cache)";
        Logger.logRequest("CACHE", methodName + ": " + finalurl);
        GetAPI.executeCache(finalurl, apiRunnable, activity, methodName);
    }

    public static void getMessages(String session, String cid, APIRunnable apiRunnable, Activity activity) {


        String finalurl = domain + "conversationmessage.getList?session=" + session + "&cid=" + cid;
        String methodName = "GETMESSAGES";
        Logger.logRequest("GET", methodName + ": " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity, methodName);
    }

    public static void sendTextMessage(String session, String message, String cid, APIRunnable apiRunnable, Activity activity) {

        message = StringFormatter.format(message);

        String finalurl = domain + "conversationmessage.send?session=" + session + "&cid=" + cid + "&text=" + message;
        String methodName = "SENDTEXTMESSAGE";
        Logger.logRequest("GET", methodName + ": " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity, methodName);
    }

    public static void createConversations(String session, String type, String name, String description, APIRunnable apiRunnable, Activity activity) {

        name = StringFormatter.format(name);
        description = StringFormatter.format(description);

        String finalurl = domain + "conversation.create?session=" + session + "&type=" + type + "&name=" + name + "&description=" + description;
        String methodName = "CREATECONV";
        Logger.logRequest("GET", methodName + ": " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity, methodName);
    }

    public static String sendNewConfirmationCode(String session) {
        String finalurl = domain + "account.sendConfirmMail?session=" + session;
        Logger.logRequest("GET", "[SENDEMAIL]: " + finalurl);
        return GET.execute(finalurl);
    }

    public static void getMyAccount(String session, APIRunnable apiRunnable, Activity activity) {
        String finalurl = domain + "account.get?session=" + session;
        String methodName = "GETMYACC";
        Logger.logRequest("GET", methodName + ": " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity, methodName);
    }

    public static void changePassword(String session, String password, String newPassword, APIRunnable apiRunnable, Activity activity) {
        password = StringFormatter.format(password);
        newPassword = StringFormatter.format(newPassword);

        String finalurl = domain + "account.changePassword?session=" + session + "&password=" + password + "&new=" + newPassword;
        String methodName = "CHANGEPASS";
        Logger.logRequest("GET", methodName + ": " + finalurl);
        GetAPI.execute(finalurl, apiRunnable, activity, methodName);
    }

    public static void createAccount(String username, String password, String email, APIRunnable apiRunnable, Activity activity) {
        String finalurl = domain + "account.create?login=" + username + "&email=" + email + "&passwordConfirm=" + password + "&password=" + password;
        String methodName = "CREATEACC";
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
