package ru.etysoft.cute.bottomsheets.conversation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ru.etysoft.cute.AlertDialog;
import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.requests.attachements.ImageFile;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cute.utils.CustomToast;
import ru.etysoft.cute.utils.ImagesWorker;
import ru.etysoft.cute.utils.Numbers;

import static android.app.Activity.RESULT_OK;

public class ConversationBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;

    // Присваемывый контент
    private String cid;
    boolean isEditing = false;

    // Нажатия на кнопки
    private View.OnClickListener passiveButtonClick = null;
    private View.OnClickListener activeButtonClick = null;

    private View view;
    String name;
    String descriptionText;
    BottomSheetBehavior bottomSheetBehavior;
    private int REQUEST_TAKE_PHOTO_FROM_GALLERY = 1;
    private String mCurrentPhotoPath;
    private ImageFile image = null;

    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Slidr.attach(getActivity());

        // Пустой фон
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    private List<MemberInfo> memberInfos = new ArrayList<>();
    private int membersCount = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);

        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();


        final FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                final CardView cardView = view.findViewById(R.id.appBar);
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    if (!isEditing) {
                      //  bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }

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

    public void setCid(String conversationId) {
        cid = conversationId;
    }

    public void loadMembers(JSONArray members) throws JSONException {
        final ListView listView = view.findViewById(R.id.members);
        int creatorid = 0;
        for (int i = 0; i < members.length(); i++) {
            JSONObject member = members.getJSONObject(i);
            int id = member.getInt("id");
            String name = member.getString("nickname");
            String role = member.getString("role");

            String photo = member.getString("photo");
            MemberInfo memberInfo = new MemberInfo(id, name, role, photo);

            if (role.equals("CREATOR")) {
                memberInfos.add(0, memberInfo);
            } else {
                memberInfos.add(memberInfo);
            }
            membersCount++;
        }

        ((LinearLayout) view.findViewById(R.id.loading)).removeAllViews();

        TextView membersCountTextView = view.findViewById(R.id.membersCount);
        membersCountTextView.setText(membersCount + " " + getString(R.string.members));
        MembersAdapter membersAdapter = new MembersAdapter(getActivity(), memberInfos);
        listView.setAdapter(membersAdapter);
    }


    public void exit() {

        Runnable toRun = new Runnable() {
            @Override
            public void run() {
                AppSettings appSettings = new AppSettings(getContext());

                APIRunnable apiRunnable = new APIRunnable() {
                    @Override
                    public void run() {
                        if (isSuccess()) {
                            dismiss();
                            getActivity().finish();
                        }
                    }
                };

                Methods.leaveConversation(appSettings.getString("session"), cid, apiRunnable, getActivity());
            }
        };
        Runnable cancel = new Runnable() {
            @Override
            public void run() {
                AppSettings appSettings = new AppSettings(getContext());

                APIRunnable apiRunnable = new APIRunnable() {
                    @Override
                    public void run() {
                    }
                };
            }
        };
        dismiss();
        AlertDialog cdd = new AlertDialog(getActivity(), getResources().getString(R.string.logout_title), getString(R.string.leave_text), toRun, cancel);
        cdd.show();
    }

    public void delete() {

        Runnable toRun = new Runnable() {
            @Override
            public void run() {
                AppSettings appSettings = new AppSettings(getContext());

                APIRunnable apiRunnable = new APIRunnable() {
                    @Override
                    public void run() {
                        if (isSuccess()) {
                            dismiss();
                            getActivity().finish();
                        }
                    }
                };

                Methods.deleteConversationLocaly(appSettings.getString("session"), cid, apiRunnable, getActivity());
            }
        };
        Runnable cancel = new Runnable() {
            @Override
            public void run() {
                AppSettings appSettings = new AppSettings(getContext());

                APIRunnable apiRunnable = new APIRunnable() {
                    @Override
                    public void run() {
                    }
                };
            }
        };
        dismiss();
        AlertDialog cdd = new AlertDialog(getActivity(), getResources().getString(R.string.clear_title), getString(R.string.clear_text), toRun, cancel);
        cdd.show();
    }

    // Задаём контент
    private void setContent() {

        APIRunnable apiRunnable = new APIRunnable() {
            @Override
            public void run() {
                if (isSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(getResponse());
                        JSONObject data = jsonObject.getJSONObject("data");
                        name = data.getString("name");
                        int id = data.getInt("id");
                        descriptionText = data.getString("description");

                        String cover = data.getString("cover");

                        JSONArray members = data.getJSONArray("members");

                        TextView nameview = view.findViewById(R.id.conv_name);
                        TextView descview = view.findViewById(R.id.conv_desc);

                        TextView nameview2 = view.findViewById(R.id.nameview);
                        TextView descview2 = view.findViewById(R.id.description);

                        TextView acronymview = view.findViewById(R.id.conv_acronym);
                        TextView acronymview2 = view.findViewById(R.id.acronym_edit);

                        ImageView icon = view.findViewById(R.id.icon);
                        ImageView icon2 = view.findViewById(R.id.icon_edit);

                        acronymview.setText(name.substring(0, 1).toUpperCase());
                        acronymview2.setText(name.substring(0, 1).toUpperCase());

                        if (!cover.equals("null")) {
                            cover = cover + "?size=300";
                            acronymview.setVisibility(View.INVISIBLE);
                            acronymview2.setText("");
                            Picasso.get().load(Methods.getPhotoUrl(cover)).placeholder(getResources().getDrawable(R.drawable.circle_gray)).memoryPolicy(MemoryPolicy.NO_CACHE).transform(new CircleTransform()).into(icon);
                            Picasso.get().load(Methods.getPhotoUrl(cover)).placeholder(getResources().getDrawable(R.drawable.circle_gray)).memoryPolicy(MemoryPolicy.NO_CACHE).transform(new CircleTransform()).into(icon2);
                        }


                        nameview.setText(name);
                        descview.setText(descriptionText);

                        nameview2.setText(name);
                        descview2.setText(descriptionText);

                        loadMembers(members);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        dismiss();
                    }
                } else {
                    dismiss();
                }
            }
        };
        AppSettings appSettings = new AppSettings(getContext());

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.acronym_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });

        view.findViewById(R.id.applybtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apply(v);
            }
        });

        Methods.getConversationInfo(appSettings.getString("session"), cid, apiRunnable, getActivity());
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

    public interface BottomSheetListener {
    }

    // Отрисовка элементов
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_conversation, container, true);
        view = v;
        ImageButton leave = v.findViewById(R.id.conv_exit);
        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });
        final ImageButton delete = v.findViewById(R.id.conv_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        setContent();

        ImageView icon = view.findViewById(R.id.icon);
        ImageView icon2 = view.findViewById(R.id.icon_edit);
        ImagesWorker.setGradient(icon, Integer.parseInt(cid));
        ImagesWorker.setGradient(icon2, Integer.parseInt(cid));


        ImageButton edit = v.findViewById(R.id.buttonEdit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardView cardView = view.findViewById(R.id.appBar);

                if (!isEditing) {
                    isEditing = true;
                    Animation fadeIn = new AlphaAnimation(1, 0);
                    fadeIn.setDuration(200);
                    fadeIn.setFillAfter(true);
                    fadeIn.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                            LinearLayout edit = view.findViewById(R.id.edit);
                            Animation fadeOut = new AlphaAnimation(0, 1);
                            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                            params.setMargins(Numbers.dpToPx(15, getContext()), 0, Numbers.dpToPx(15, getContext()), 0);
                            edit.setLayoutParams(params);
                            fadeOut.setDuration(200);
                            fadeOut.setFillAfter(true);
                            edit.startAnimation(fadeOut);
                            edit.setVisibility(View.VISIBLE);
                            ((LinearLayout) view.findViewById(R.id.content)).removeAllViews();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    params.setMargins(0, 0, 0, Numbers.dpToPx(-20, getContext()));
                    ((CardView) view.findViewById(R.id.appBar)).setLayoutParams(params);
                    bottomSheetBehavior.setDraggable(false);
                    view.findViewById(R.id.content).startAnimation(fadeIn);
                    bottomSheetBehavior.setFitToContents(false);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    cardView.setRadius(Numbers.dpToPx(0, getContext()));


                    //TODO: нормальные названгея


                }


//                    Intent intent = new Intent(getActivity(), EditChat.class);
//
//
//                    ImageButton deleteBtn = view.findViewById(R.id.conv_delete);
//                    ImageButton exitBtn = view.findViewById(R.id.conv_exit);
//                    TextView acr = view.findViewById(R.id.conv_acronym);
//
//
//                    Pair<View, String> p1 = Pair.create((View) imageView, "editchat");
//                    Pair<View, String> p2 = Pair.create((View) deleteBtn, "delbtn");
//                    Pair<View, String> p3 = Pair.create((View) exitBtn, "exitbtn");
//                    Pair<View, String> p4 = Pair.create((View) acr, "acronym");
//
//                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p1, p2, p3, p4);
//                    startActivity(intent, options.toBundle());
            }
        });

        return v;
    }

    private ImageFile createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        ImageFile image = new ImageFile(File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        ).getAbsolutePath());

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_TAKE_PHOTO_FROM_GALLERY);
    }

    public void apply(View v) {
        HashMap<String, Object> params = new HashMap<String, Object>();

        TextView nameView = view.findViewById(R.id.nameview);
        TextView descriptionView = view.findViewById(R.id.description);

        final Button applyButton = view.findViewById(R.id.applybtn);
        final ProgressBar wait = view.findViewById(R.id.loadingApply);
        applyButton.setVisibility(View.INVISIBLE);
        wait.setVisibility(View.VISIBLE);

        final AppSettings appSettings = new AppSettings(getActivity());

        params.put("session", appSettings.getString("session"));
        params.put("cid", cid);

        if (image != null) {
            params.put("photo", image);
        }

        if (!String.valueOf(nameView.getText()).equals(name)) {
            params.put("name", String.valueOf(nameView.getText()));
        }

        if (!String.valueOf(descriptionView.getText()).equals(descriptionText)) {
            params.put("description", String.valueOf(descriptionView.getText()));
        }

        APIRunnable apiRunnable = new APIRunnable() {
            @Override
            public void run() {
                super.run();
                applyButton.setVisibility(View.VISIBLE);
                wait.setVisibility(View.INVISIBLE);
                if (isSuccess()) {
                    dismiss();
                } else {
                    if (Methods.hasInternet(getContext())) {
                        CustomToast.show(getResources().getString(R.string.err_unknown), R.drawable.icon_error, getActivity());
                    } else {
                        CustomToast.show(getResources().getString(R.string.err_no_internet), R.drawable.icon_error, getActivity());
                    }
                }
            }
        };


        Methods.editChat(params, apiRunnable, getActivity());


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO_FROM_GALLERY && resultCode == RESULT_OK) {
            try {
                // Creating file
                ImageFile photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.d("ACTIVITYRES", "Error occurred while creating the file");
                }

                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                // Copying
                copyStream(inputStream, fileOutputStream);
                fileOutputStream.close();
                inputStream.close();
                ImageView imageView = view.findViewById(R.id.icon_edit);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);
                image = photoFile;
                int dp = Numbers.dpToPx(150, getContext());
                imageView.setImageBitmap(ImagesWorker.getCircleCroppedBitmap(bitmap, dp, dp));
                TextView acronymview2 = view.findViewById(R.id.acronym_edit);
                acronymview2.setVisibility(View.INVISIBLE);

            } catch (Exception e) {
                Log.d("ACTIVITYRES", "onActivityResult: " + e.toString());
            }
        }
    }


}
