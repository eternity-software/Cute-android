package ru.etysoft.cute;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlertDialog extends Dialog implements android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    public TextView titleview, textview;
    public Runnable positive = null;
    public Runnable negative = null;
    private String text, title;

    public AlertDialog(Activity a, String title, String text, Runnable positive, Runnable negative) {
        super(a);

        this.c = a;

        this.text = text;
        this.title = title;
        this.positive = positive;
        this.negative = negative;
    }

    public AlertDialog(Activity a, String title, String text, Runnable positive) {
        super(a);

        this.c = a;

        this.text = text;
        this.title = title;
        this.positive = positive;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        yes = (Button) findViewById(R.id.dialog_active);
        no = (Button) findViewById(R.id.dialog_passive);
        titleview = findViewById(R.id.title_dialog);
        textview = findViewById(R.id.dialog_text);
        titleview.setText(title);
        textview.setText(text);

        if (negative == null) {
            LinearLayout mother = findViewById(R.id.motherButtons);
            mother.removeView(no);
        }


        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialog_active) {
            positive.run();
        } else if (id == R.id.dialog_passive) {
            negative.run();
        }
        dismiss();
    }
}