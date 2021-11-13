package ru.etysoft.cute.bottomsheets.filepicker;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Collections;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.ImageEdit.ImageEdit;
import ru.etysoft.cute.components.SmartImageView;
import ru.etysoft.cute.images.WaterfallBalancer;
import ru.etysoft.cute.utils.Logger;

public class FilePickerBottomSheet extends BottomSheetDialogFragment {
    private View view;
    private ArrayList<String> images;
    private boolean isShown = false;

    private FilePickerAdapter filePickerAdapter;

    public ArrayList<String> getImages() {
        return filePickerAdapter.getImages();
    }

    private FilePickerBottomSheet.ItemClickListener onItemClickListener;

    public static interface ItemClickListener
    {
        void onItemClick(int pos, View view);
    }


    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        super.show(manager, tag);
        Logger.logActivity("Showed bs");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.bottom_sheet_filespicker, container, true);
        view = v;

        return v;
    }



    public void setRunnable(FilePickerBottomSheet.ItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
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

        RecyclerView gallery = bottomSheet.findViewById(R.id.gridView);
        gallery.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        filePickerAdapter = new FilePickerAdapter(getActivity(), onItemClickListener, gallery);

        gallery.setAdapter(filePickerAdapter);


    }


}
