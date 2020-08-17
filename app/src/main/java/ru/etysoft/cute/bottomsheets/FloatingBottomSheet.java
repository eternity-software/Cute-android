package ru.etysoft.cute.bottomsheets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.xmlpull.v1.XmlPullParser;

import ru.etysoft.cute.R;

public class FloatingBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;

    // Присваемывый контент
    private Drawable icon;
    private String title;
    private String text;
    private String active_button;
    private String passive_button;

    // Нажатия на кнопки
    private View.OnClickListener passiveButtonClick = null;
    private View.OnClickListener activeButtonClick = null;

    private View view;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Пустой фон
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);

        // Кастомная анимация
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();

        // Получение поведения
        FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);

        // Ловим COLLAPSED и не даём промежуточному положению существовать
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View view, int i) {
                     if(i == BottomSheetBehavior.STATE_COLLAPSED) {
                         getDialog().cancel();
                     }
                     else if(i == BottomSheetBehavior.STATE_HIDDEN) {
                         getDialog().cancel();
                     }
                }

                @Override
                public void onSlide(@NonNull View view, float v) {

                }
            });

    }


    // Задаём контент
    public void setContent(Drawable image, String title, String text, String button_text1, String button_text2, View.OnClickListener passiveButtonClick, View.OnClickListener activeButtonClick)
    {
       icon = image;
       this.title = title;
       this.text = text;
       passive_button = button_text1;
       active_button = button_text2;

       this.passiveButtonClick = passiveButtonClick;
       this.activeButtonClick = activeButtonClick;
    }


    // Отрисовка элементов
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_floating, container, true);
        view = v;

        LinearLayout mother = v.findViewById(R.id.motherButtons);

        ImageView imageView = view.findViewById(R.id.bottomImage);
        imageView.setImageDrawable(icon);

        TextView titleView = view.findViewById(R.id.BottomTitle);
        titleView.setText(title);

        TextView textView = view.findViewById(R.id.BottomText);
        textView.setText(text);

        TextView buttonActive = view.findViewById(R.id.ButtonActive);
        if(activeButtonClick == null)
        {
            mother.removeView(buttonActive);
        }
        else
        {
            buttonActive.setText(active_button);
            buttonActive.setOnClickListener(activeButtonClick);
        }

        TextView buttonPassive = view.findViewById(R.id.ButtonPassive);
        if(passiveButtonClick == null)
        {
            mother.removeView(buttonPassive);
        }
        else
        {
            buttonPassive.setText(passive_button);
            buttonPassive.setOnClickListener(passiveButtonClick);
        }

        return v;
    }

    public interface BottomSheetListener {
        void onButtonClicked(String text);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

                FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        return dialog;
    }

}
