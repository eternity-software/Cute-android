package ru.etysoft.cute.bottomsheets.filepicker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.camera.CameraActivity;
import ru.etysoft.cute.images.WaterfallBalancer;
import ru.etysoft.cute.utils.Logger;
import ru.etysoft.cute.utils.Numbers;

public class FilePickerBottomSheet extends BottomSheetDialogFragment {
    private View view;
    private ArrayList<String> images;
    private boolean isShown = false;

    private FilePickerAdapter filePickerAdapter;

    public ArrayList<FileInfo> getMedia() {
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

        v.findViewById(R.id.openCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CameraActivity.class);

                getActivity().startActivity(intent);

            }
        });

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



        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();


        final FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);



        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);


        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                final CardView cardView = view.findViewById(R.id.appBar);


                float pix = Numbers.dpToPx(20, getContext()) * (1 - slideOffset);
                float maxPix = Numbers.dpToPx(20, getContext());

                if(pix < maxPix)
                {
                    cardView.setRadius(pix);
                }
                else
                {
                    cardView.setRadius(maxPix);
                }



            }
        });

        RecyclerView gallery = bottomSheet.findViewById(R.id.gridView);
        gallery.setLayoutManager(new GridLayoutManager(getActivity(), 3));


        final TextView debugText = view.findViewById(R.id.debugText);
        filePickerAdapter = new FilePickerAdapter(getActivity(), onItemClickListener, gallery);
        filePickerAdapter.setBalancerCallback(new WaterfallBalancer.BalancerCallback() {
            @Override
            public void onActiveWaterfallsCountChange(final int count) {
              getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        debugText.setText("Active waterfalls: " + count);
                    }
                });
            }
        });

        gallery.setAdapter(filePickerAdapter);


    }


}
