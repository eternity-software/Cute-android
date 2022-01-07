package ru.etysoft.cute;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AlertDialog extends Dialog implements android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    public TextView titleview, textview;


    private final String text;
    private final String title;
    private TextView inputView;

    private DialogHandler dialogHandler;

    public interface DialogHandler
    {
        void onPositiveClicked(String input);
        void onNegativeClicked(String input);
        void onClosed(String input);

    }

    public AlertDialog(Activity a, String title, String text, DialogHandler dialogHandler) {
        super(a);

        this.dialogHandler = dialogHandler;
        this.c = a;

        this.text = text;
        this.title = title;

    }


    public void removeNegativeButton()
    {
        LinearLayout mother = findViewById(R.id.motherButtons);
        mother.removeView(no);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        yes = findViewById(R.id.dialog_active);
        no = findViewById(R.id.dialog_passive);
        titleview = findViewById(R.id.title_dialog);
        textview = findViewById(R.id.dialog_text);
        inputView = findViewById(R.id.alertInput);
        titleview.setText(title);
        textview.setText(text);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialogHandler.onClosed(String.valueOf(inputView.getText()));
            }
        });


        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialog_active) {
            dialogHandler.onPositiveClicked(String.valueOf(inputView.getText()));
        } else if (id == R.id.dialog_passive) {
            dialogHandler.onNegativeClicked(String.valueOf(inputView.getText()));
        }
        dismiss();
    }
}