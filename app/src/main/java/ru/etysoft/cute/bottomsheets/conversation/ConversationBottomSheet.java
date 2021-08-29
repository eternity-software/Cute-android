package ru.etysoft.cute.bottomsheets.conversation;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
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
import com.squareup.picasso.Picasso;

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
import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.ImagePreview;
import ru.etysoft.cute.activities.Profile;
import ru.etysoft.cute.activities.editprofile.EditProfileActivity;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.components.CuteToast;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cute.utils.ImagesWorker;
import ru.etysoft.cute.utils.Numbers;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.chat.AddMember.AddMemberRequest;
import ru.etysoft.cuteframework.methods.chat.AddMember.AddMemberResponse;
import ru.etysoft.cuteframework.methods.chat.ChangeAvatar.ChangeAvatarRequest;
import ru.etysoft.cuteframework.methods.chat.ChangeAvatar.ChangeAvatarResponse;
import ru.etysoft.cuteframework.methods.account.ChangeCover.ChangeCoverRequest;
import ru.etysoft.cuteframework.methods.account.ChangeCover.ChangeCoverResponse;
import ru.etysoft.cuteframework.methods.chat.ChatMember;
import ru.etysoft.cuteframework.methods.chat.ClearHistory.ClearHistoryRequest;
import ru.etysoft.cuteframework.methods.chat.ClearHistory.ClearHistoryResponse;
import ru.etysoft.cuteframework.methods.chat.GetInfo.ChatInfoRequest;
import ru.etysoft.cuteframework.methods.chat.GetInfo.ChatInfoResponse;
import ru.etysoft.cuteframework.methods.chat.Leave.ChatLeaveRequest;
import ru.etysoft.cuteframework.methods.chat.Leave.ChatLeaveResponse;
import ru.etysoft.cuteframework.methods.media.UploadImageRequest;
import ru.etysoft.cuteframework.methods.media.UploadImageResponse;
import ru.etysoft.cuteframework.requests.attachements.ImageFile;

public class ConversationBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;

    // Присваемывый контент
    private String cid;
    boolean isEditing = false;

    // Нажатия на кнопки
    private View.OnClickListener passiveButtonClick = null;
    private View.OnClickListener activeButtonClick = null;

    private Activity activity;
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
        activity = getActivity();
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    public void addMember()
    {
        final String[] text = new String[1];
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Invite member by id");

// Set up the input
        final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            AddMemberResponse addMemberResponse = (new AddMemberRequest(CachedValues.getSessionKey(getActivity()), cid, input.getText().toString())).execute();
                            if(addMemberResponse.isSuccess())
                            {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismiss();
                                    }
                                });

                            }

                        } catch (ResponseException e) {
                            e.printStackTrace();
                        } catch (final Exception e) {
                            e.printStackTrace();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CuteToast.showError(e.getMessage(), getActivity());
                                }
                            });
                        }
                    }
                });
                thread.start();
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
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


    }

    public void setCid(String conversationId) {
        cid = conversationId;
    }

    public void loadData() {
        final TextView chatNameView = view.findViewById(R.id.conv_name);
        final TextView chatDescriptionView = view.findViewById(R.id.conv_desc);
        final ImageButton addMemberButton = view.findViewById(R.id.buttonAdd);
        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMember();
            }
        });
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ChatInfoResponse chatInfoResponse = (new ChatInfoRequest(CachedValues.getSessionKey(getContext()), cid)).execute();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Avatar avatar = view.findViewById(R.id.icon);

                                if(chatInfoResponse.getChat().getAvatarPath() != null)
                                {
                                    Picasso.get().load(chatInfoResponse.getChat().getAvatarPath()).transform(new CircleTransform()).into(avatar.getPictureView());
                                    avatar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                Intent intent = new Intent(getActivity(), ImagePreview.class);
                                                intent.putExtra("url", chatInfoResponse.getChat().getAvatarPath());
                                                startActivity(intent);
                                            } catch (ResponseException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                }
                                avatar.setAcronym(chatInfoResponse.getChat().getName(), Avatar.Size.MEDIUM);
                                avatar.generateIdPicture(chatInfoResponse.getChat().getId());
                                chatNameView.setText(chatInfoResponse.getChat().getName());
                                chatDescriptionView.setText(chatInfoResponse.getChat().getDescription());
                                for (ChatMember chatMember : chatInfoResponse.getMembers()) {
                                    MemberInfo memberInfo = new MemberInfo(chatMember.getId(),
                                            chatMember.getDisplayName(),
                                            chatMember.getType(), chatMember.getPhoto());

                                    if (memberInfo.getRole().equals(ChatMember.Types.CREATOR)) {
                                        memberInfos.add(0, memberInfo);
                                    } else if (memberInfo.getRole().equals(ChatMember.Types.ADMIN)) {
                                        memberInfos.add(1, memberInfo);
                                    } else {
                                        memberInfos.add(memberInfo);
                                    }
                                    membersCount++;
                                }
                                ((LinearLayout) view.findViewById(R.id.loading)).removeAllViews();

                                TextView membersCountTextView = view.findViewById(R.id.membersCount);
                                membersCountTextView.setText(membersCount + " " + getString(R.string.members));
                                MembersAdapter membersAdapter = new MembersAdapter(getActivity(), memberInfos);
                                ((ListView) view.findViewById(R.id.members)).setAdapter(membersAdapter);

                            } catch (ResponseException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                } catch (ResponseException e) {
                    e.printStackTrace();
                } catch (NotCachedException e) {
                    e.printStackTrace();
                }
                catch (final Exception e)
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CuteToast.showError(e.getMessage(), getActivity());
                            dismiss();
                        }
                    });
                }
            }
        });
        thread.start();


    }


    public void leaveChat() {
        final String token;
        try {
            token = CachedValues.getSessionKey(getActivity());
            Runnable toRun = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ChatLeaveResponse chatLeaveResponse = (new ChatLeaveRequest(token, cid)).execute();
                                    if (chatLeaveResponse.isSuccess()) {
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                activity.finish();

                                            }
                                        });

                                    }
                                } catch (ResponseException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        thread.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            Runnable cancel = new Runnable() {
                @Override
                public void run() {


                }
            };

            AlertDialog leaveDialog = new AlertDialog(getActivity(), getResources().getString(R.string.leave_title), getString(R.string.leave_text), toRun, cancel);
            leaveDialog.show();
            dismiss();
        } catch (NotCachedException e) {
            e.printStackTrace();
        }

    }

    public void deleteChat() {

        final String token;
        try {
            token = CachedValues.getSessionKey(getActivity());

            Runnable toRun = new Runnable() {
                @Override
                public void run() {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ClearHistoryResponse clearHistoryResponse = (new ClearHistoryRequest(token, cid)).execute();
                                if (clearHistoryResponse.isSuccess()) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            activity.finish();
                                        }
                                    });

                                }
                            } catch (ResponseException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    thread.start();
                }
            };
            Runnable cancel = new Runnable() {
                @Override
                public void run() {


                }
            };
            dismiss();
            AlertDialog deleteDialog = new AlertDialog(getActivity(), getResources().getString(R.string.clear_title), getString(R.string.clear_text), toRun, cancel);
            deleteDialog.show();
        } catch (NotCachedException e) {
            e.printStackTrace();
        }

    }

    // Задаём контент
    private void setContent() {

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
        loadData();
        ImageButton leave = v.findViewById(R.id.conv_exit);
        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveChat();
            }
        });
        final ImageButton delete = v.findViewById(R.id.conv_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteChat();
            }
        });
        setContent();


        ImageView icon2 = view.findViewById(R.id.icon_edit);

        ImagesWorker.setGradient(icon2, Integer.parseInt(cid));

        icon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });


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
        HashMap<String, String> params = new HashMap<String, String>();

        TextView nameView = view.findViewById(R.id.nameview);
        TextView descriptionView = view.findViewById(R.id.description);

        final Button applyButton = view.findViewById(R.id.applybtn);
        final ProgressBar wait = view.findViewById(R.id.loadingApply);
        applyButton.setVisibility(View.INVISIBLE);
        wait.setVisibility(View.VISIBLE);


        params.put("cid", cid);

        if (image != null) {
            // params.put("photo", image);
        }

        if (!String.valueOf(nameView.getText()).equals(name)) {
            params.put("name", String.valueOf(nameView.getText()));
        }

        if (!String.valueOf(descriptionView.getText()).equals(descriptionText)) {
            params.put("description", String.valueOf(descriptionView.getText()));
        }

        // TODO: edit


    }


    @Override
    public void onActivityResult(final int requestCode, int resultCode, @Nullable Intent data) {
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
                Thread upload = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            UploadImageResponse uploadImageResponse = (new UploadImageRequest(image, CachedValues.getSessionKey(getActivity()))).execute();
                            String mediaId = uploadImageResponse.getMediaId();
                            if(requestCode == REQUEST_TAKE_PHOTO_FROM_GALLERY) {
                                ChangeAvatarResponse changeAvatarResponse = (new ChangeAvatarRequest(CachedValues.getSessionKey(getActivity()), mediaId, cid)).execute();
                                if (changeAvatarResponse.isSuccess()) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            CuteToast.showSuccess("uspeshno!", getActivity());
                                        }
                                    });
                                }
                            }

                        } catch (ResponseException | NotCachedException e) {
                            e.printStackTrace();
                        }
                        catch (final Exception e)
                        {
                            e.printStackTrace();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CuteToast.showError(e.getMessage(), getActivity());
                                    dismiss();
                                }
                            });


                        }
                    }
                });
                upload.start();

            } catch (Exception e) {
                Log.d("ACTIVITYRES", "onActivityResult: " + e.toString());
            }
        }
    }


}
