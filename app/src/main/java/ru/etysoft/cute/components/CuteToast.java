package ru.etysoft.cute.components;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ru.etysoft.cute.R;
import ru.etysoft.cute.utils.Numbers;

public class CuteToast {


    public static void show(String message, int icon, Activity context) {

        LayoutInflater inflater = context.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) context.findViewById(R.id.toast_layout_root));

        ImageView image = (ImageView) layout.findViewById(R.id.image);
        image.setImageResource(icon);
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, Numbers.dpToPx(20, context.getApplicationContext()));
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

    }

    public static void showSuccess(String message, Activity context) {
        show(message, R.drawable.icon_success, context);
    }

    public static void showError(String message, Activity context) {
        show(message, R.drawable.icon_error, context);
    }
}
