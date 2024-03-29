package ru.etysoft.cute.bottomsheets.conversation;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ru.etysoft.cute.AlertDialog;
import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.ImagePreview;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.components.CuteToast;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cute.images.ImagesWorker;
import ru.etysoft.cute.utils.Numbers;
import ru.etysoft.cute.utils.SliderActivity;
import ru.etysoft.cuteframework.exceptions.NoSuchValueException;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.BlankResponse;
import ru.etysoft.cuteframework.methods.chat.AddMember.AddMemberResponse;
import ru.etysoft.cuteframework.methods.chat.AddMemberRequest;
import ru.etysoft.cuteframework.methods.chat.ChangeAvatar.ChangeAvatarRequest;
import ru.etysoft.cuteframework.methods.chat.ChangeAvatar.ChangeAvatarResponse;
import ru.etysoft.cuteframework.methods.chat.ChatClearHistory;
import ru.etysoft.cuteframework.methods.chat.ChatGetInfoRequest;
import ru.etysoft.cuteframework.methods.chat.ChatGetMembersRequest;
import ru.etysoft.cuteframework.methods.chat.ChatLeaveRequest;
import ru.etysoft.cuteframework.methods.chat.ClearHistory.ClearHistoryRequest;
import ru.etysoft.cuteframework.methods.chat.ClearHistory.ClearHistoryResponse;
import ru.etysoft.cuteframework.models.ChatMember;
import ru.etysoft.cuteframework.requests.attachements.ImageFile;

public class ConversationBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;

    // Присваемывый контент
    private String cid;
    boolean isEditing = false;

    // Нажатия на кнопки
    private final View.OnClickListener passiveButtonClick = null;
    private final View.OnClickListener activeButtonClick = null;

    private Activity activity;
    private View view;
    String name;
    String descriptionText;
    BottomSheetBehavior bottomSheetBehavior;
    private final int REQUEST_TAKE_PHOTO_FROM_GALLERY = 1;
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

    public void addMember() {
        final String[] text = new String[1];
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Invite member by id");

        final EditText input = new EditText(getContext());

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BlankResponse addMemberResponse = (new AddMemberRequest(input.getText().toString(), cid)).execute();
                            if (addMemberResponse.isSuccess()) {
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

    private final List<MemberInfo> memberInfos = new ArrayList<>();
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
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {


                    // cardView.setRadius(Numbers.dpToPx(20, getContext()));
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


    }

    @Override
    public void dismiss() {
        super.dismiss();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        SliderActivity sliderActivity = new SliderActivity();
        sliderActivity.attachSlider(getActivity());
    }

    public void setCid(String conversationId) {
        cid = conversationId;
    }

    public void loadData() {
        final TextView chatNameView = view.findViewById(R.id.conv_name);
        final TextView chatDescriptionView = view.findViewById(R.id.conv_desc);
        //final ImageView addMemberButton = view.findViewById(R.id.buttonAdd);
//        addMemberButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addMember();
//            }
//        });
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ChatGetInfoRequest.ChatGetInfoResponse chatInfoResponse = (new ChatGetInfoRequest(cid)).execute();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Avatar avatar = view.findViewById(R.id.icon);


                                // TODO: add avatar check

                                boolean hasAvatar = false;
                                if (hasAvatar) {
                                    //Picasso.get().load(chatInfoResponse.getChat().getAvatar()).transform(new CircleTransform()).into(avatar.getPictureView());
                                    avatar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                                Intent intent = new Intent(getActivity(), ImagePreview.class);
                                               // intent.putExtra("url", chatInfoResponse.getChat().getAvatar());
                                                startActivity(intent);


                                        }
                                    });
                                }

                                avatar.setAcronym(chatInfoResponse.getChat().getName(), Avatar.Size.MEDIUM);
                                avatar.generateIdPicture(1);
                                chatNameView.setText(chatInfoResponse.getChat().getName());
                                chatDescriptionView.setText(chatInfoResponse.getChat().getDescription());

//                                for (ChatMember chatMember : chatInfoResponse.get()) {
//                                    MemberInfo memberInfo = new MemberInfo(chatMember.getId(),
//                                            chatMember.getDisplayName(),
//                                            chatMember.getType(), chatMember.getAvatar());
//
//                                    if (memberInfo.getRole().equals(ChatMember.Types.CREATOR)) {
//                                        memberInfos.add(0, memberInfo);
//                                    } else if (memberInfo.getRole().equals(ChatMember.Types.ADMIN)) {
//                                        memberInfos.add(1, memberInfo);
//                                    } else {
//                                        memberInfos.add(memberInfo);
//                                    }
//                                    membersCount++;
//                                }

                                ((LinearLayout) view.findViewById(R.id.loading)).removeAllViews();

                                TextView membersCountTextView = view.findViewById(R.id.membersCount);
                                membersCountTextView.setText(membersCount + " " + getString(R.string.members));
                                MembersAdapter membersAdapter = new MembersAdapter(getActivity(), memberInfos);
                                ((ListView) view.findViewById(R.id.members)).setAdapter(membersAdapter);

                            } catch ( NoSuchValueException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    final ChatGetMembersRequest.ChatGetMembersResponse chatGetMembersResponse = (new ChatGetMembersRequest(cid)).execute();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (ChatMember chatMember : chatGetMembersResponse.getChatMembers()) {
                                        MemberInfo memberInfo = new MemberInfo(chatMember.getAccountId(),
                                                chatMember.getName(),
                                                chatMember.getPermissionsGroup(), null);

                                        if (memberInfo.getRole().equals(ChatMember.TYPE_OWNER)) {
                                            memberInfos.add(0, memberInfo);
                                        } else if (memberInfo.getRole().equals(ChatMember.TYPE_ADMIN)) {
                                            memberInfos.add(1, memberInfo);
                                        } else {
                                            memberInfos.add(memberInfo);
                                        }
                                        membersCount++;
                                    }
                            } catch (NoSuchValueException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CuteToast.showError(e.getMessage(), getActivity());
                              //  dismiss();
                            }
                        });
                    }

                }
            }
        });
        thread.start();


    }


    public void leaveChat() {



            AlertDialog leaveDialog = new AlertDialog(getActivity(), getResources().getString(R.string.leave_title), getString(R.string.leave_text), new AlertDialog.DialogHandler() {
                @Override
                public void onPositiveClicked(String input) {
                    try {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    BlankResponse chatLeaveResponse = (new ChatLeaveRequest(cid)).execute();
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
                                } catch (ru.etysoft.cuteframework.exceptions.NotCachedException e) {
                                    e.printStackTrace();
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }

                            }
                        });
                        thread.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onNegativeClicked(String input) {

                }

                @Override
                public void onClosed(String input) {

                }
            });
            leaveDialog.show();
            dismiss();

    }

    public void deleteChat() {

            dismiss();
            AlertDialog deleteDialog = new AlertDialog(getActivity(), getResources().getString(R.string.clear_title), getString(R.string.clear_text),
                    new AlertDialog.DialogHandler() {
                        @Override
                        public void onPositiveClicked(String input) {
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        BlankResponse clearHistoryResponse = (new ChatClearHistory(cid)).execute();
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
                                    } catch (ru.etysoft.cuteframework.exceptions.NotCachedException e) {
                                        e.printStackTrace();
                                    } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                    }
                                }
                            });
                            thread.start();

                        }

                        @Override
                        public void onNegativeClicked(String input) {

                        }

                        @Override
                        public void onClosed(String input) {

                        }
                    });
            deleteDialog.show();


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
        try {


            ImageView leave = v.findViewById(R.id.conv_exit);
            leave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    leaveChat();
                }
            });
            final ImageView delete = v.findViewById(R.id.conv_delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteChat();
                }
            });

            final ImageView addMember = v.findViewById(R.id.buttonAdd);
            addMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addMember();
                }
            });
            setContent();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        return v;
    }


}
