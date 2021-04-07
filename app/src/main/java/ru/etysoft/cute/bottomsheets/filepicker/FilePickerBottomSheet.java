package ru.etysoft.cute.bottomsheets.filepicker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import ru.etysoft.cute.R;

public class FilePickerBottomSheet extends BottomSheetDialogFragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_filespicker, container, true);
        view = v;

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Пустой фон
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);

        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();


        final FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                final CardView cardView = view.findViewById(R.id.appBar);
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {


                    if (cardView.getRadius() != 0) {
                        Thread animation = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int radius = 20; radius >= 0; radius = radius - 1) {
                                    System.out.print(radius);
                                    final int finalRadius = radius;
                                    try {
                                        Thread.sleep(10);
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //    cardView.setRadius(Numbers.dpToPx(finalRadius, getContext()));
                                            }
                                        });

                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });
                        animation.start();
                    }
                } else {

                    // cardView.setRadius(Numbers.dpToPx(20, getContext()));
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


    }

}
