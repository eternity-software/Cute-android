package ru.etysoft.cute.exceptions;

import android.content.Context;
import android.content.Intent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import ru.etysoft.cute.activities.CrashReportActivity;

public class CrashExceptionHandler implements UncaughtExceptionHandler {

    private UncaughtExceptionHandler defaultUEH;


    private Context context;


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

        // NotificationService.notifyBanner(context, "Cute crashed!", stacktrace);
        Intent snoozeIntent = new Intent(context, CrashReportActivity.class);
        snoozeIntent.putExtra("stacktrace", stacktrace);
        context.startActivity(snoozeIntent);

        defaultUEH.uncaughtException(t, e);
    }


}