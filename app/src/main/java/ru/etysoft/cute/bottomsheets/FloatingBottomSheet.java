package ru.etysoft.cute.bottomsheets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

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
        final FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);

        // Ловим COLLAPSED и не даём промежуточному положению существовать, а так же убираем слайд при невозможности отменить
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_DRAGGING) {
                    if (!isCancelable()) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }
                if (i == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (isCancelable()) {
                        getDialog().cancel();
                        getDialog().dismiss();
                    } else {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                } else if (i == BottomSheetBehavior.STATE_HIDDEN) {
                    getDialog().cancel();
                    getDialog().dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });


    }


    // Задаём контент
    public void setContent(Drawable image, String title, String text) {
        icon = image;
        this.title = title;
        this.text = text;
    }

    // Задаём контент
    public void setContent(Drawable image, String title, String text, String active_button_text, String passive_button_text, View.OnClickListener activeButtonClick, View.OnClickListener passiveButtonClick) {
        icon = image;
        this.title = title;
        this.text = text;
        passive_button = passive_button_text;
        active_button = active_button_text;

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


}
