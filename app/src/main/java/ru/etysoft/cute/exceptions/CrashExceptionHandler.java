package ru.etysoft.cute.exceptions;

import android.content.Context;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import ru.etysoft.cute.services.NotificationService;

public class CrashExceptionHandler implements UncaughtExceptionHandler {

    private UncaughtExceptionHandler defaultUEH;


    private Context context;

    /*
     * if any of the parameters is null, the respective functionality
     * will not be used
     */
    public CrashExceptionHandler(Context context) {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.context = context;
    }

    public void uncaughtException(Thread t, Throwable e) {

        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();

        NotificationService.notifyBanner(context, "Cute crashed!", stacktrace);

        defaultUEH.uncaughtException(t, e);
    }


}