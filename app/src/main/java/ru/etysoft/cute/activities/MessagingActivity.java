package ru.etysoft.cute.activities;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.WindowInsetsAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.math.MathUtils;
import com.r0adkll.slidr.util.ViewDragHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.chatslist.ChatsListAdapter;
import ru.etysoft.cute.activities.imagesend.ImageSendActivity;
import ru.etysoft.cute.activities.messages.MessagesAdapter;
import ru.etysoft.cute.bottomsheets.conversation.ConversationBottomSheet;
import ru.etysoft.cute.bottomsheets.filepicker.FilePickerBottomSheet;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.components.CuteToast;
import ru.etysoft.cute.components.ErrorPanel;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cute.utils.ImagesWorker;
import ru.etysoft.cute.utils.SendorsControl;
import ru.etysoft.cute.utils.SliderActivity;
import ru.etysoft.cuteframework.data.APIKeys;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.chat.GetHistory.GetMessageListRequest;
import ru.etysoft.cuteframework.methods.chat.GetHistory.GetMessageListResponse;
import ru.etysoft.cuteframework.methods.chat.GetInfo.ChatInfoRequest;
import ru.etysoft.cuteframework.methods.chat.GetInfo.ChatInfoResponse;
import ru.etysoft.cuteframework.methods.chat.SendMessage.SendMessageRequest;
import ru.etysoft.cuteframework.methods.chat.SendMessage.SendMessageResponse;
import ru.etysoft.cuteframework.methods.media.UploadImageRequest;
import ru.etysoft.cuteframework.methods.media.UploadImageResponse;
import ru.etysoft.cuteframework.methods.messages.Message;
import ru.etysoft.cuteframework.methods.user.User;
import ru.etysoft.cuteframework.requests.attachements.ImageFile;
import ru.etysoft.cuteframework.sockets.methods.Messages.MessagesSocket;

public class MessagingActivity extends AppCompatActivity implements ConversationBottomSheet.BottomSheetListener {

    private final List<Message> messageList = new ArrayList<>();
    private final Map<String, Message> ids = new HashMap<String, Message>();
    private MessagesAdapter adapter;
    private MessagesSocket messagesSocket;

    private int chatId = 1;
    private String name = "42";
    private String avatar = null;
    private final String countMembers = "42";
    private boolean isDialog = false;
    private ErrorPanel errorPanel;
    public boolean isVoice = true;
    private String mediaIdToSend;
    private String mediaPathToSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        chatId = getIntent().getIntExtra(APIKeys.CHAT_ID, 0);
        name = getIntent().getStringExtra(APIKeys.NAME);
        avatar = getIntent().getStringExtra(APIKeys.AVATAR);
        isDialog = getIntent().getBooleanExtra("isd", false);

        Avatar picture = findViewById(R.id.avatar);
        picture.generateIdPicture(chatId);

        if (avatar != null) {
            Picasso.get().load(avatar).transform(new CircleTransform()).into(picture.getPictureView());
        }

        picture.setAcronym((name), Avatar.Size.SMALL);

        final RecyclerView recyclerView = findViewById(R.id.messages);
        adapter = new MessagesAdapter(this, messageList, isDialog, recyclerView);
        recyclerView.setAdapter(adapter);

        TextView subtitle = findViewById(R.id.subtitle);
        if (!isDialog) {
            subtitle.setText(countMembers + " " + getResources().getString(R.string.members));
        }

        errorPanel = findViewById(R.id.error_panel);
        errorPanel.getRootView().setVisibility(View.INVISIBLE);

        final LinearLayout error = findViewById(R.id.error);

        error.setVisibility(View.INVISIBLE);
        errorPanel.setReloadAction(new Runnable() {
            @Override
            public void run() {
                errorPanel.hide(new Runnable() {
                    @Override
                    public void run() {
                        processListUpdate();
                        error.setVisibility(View.INVISIBLE);

                    }
                });
            }
        });

        TextView title = findViewById(R.id.title);
        title.setText(name);
        setupVoiceButton();


        setupOnTextInput();
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);


        processListUpdate();
        loadChatInfo();
        registerSocket();

        final ViewGroup rootView = (ViewGroup) findViewById(R.id.rootView);

        if (Build.VERSION.SDK_INT >= 30) {
            rootView.setWindowInsetsAnimationCallback(new WindowInsetsAnimation.Callback(WindowInsetsAnimation.Callback.DISPATCH_MODE_STOP) {
                float startBottom = 0;
                float endBottom = 0;

                @NonNull
                @Override
                public WindowInsetsAnimation.Bounds onStart(@NonNull WindowInsetsAnimation animation, @NonNull WindowInsetsAnimation.Bounds bounds) {
                    startBottom = rootView.getBottom();
                    return super.onStart(animation, bounds);
                }

                @Override
                public void onPrepare(@NonNull WindowInsetsAnimation animation) {
                    super.onPrepare(animation);
                    endBottom = rootView.getBottom();
                    rootView.setTranslationY(startBottom - endBottom);
                }

                @NonNull
                @Override
                public WindowInsets onProgress(@NonNull WindowInsets insets, @NonNull List<WindowInsetsAnimation> runningAnimations) {
                    float offset = MathUtils.lerp(startBottom - endBottom,
                            0,
                            android.R.anim.decelerate_interpolator);

                    rootView.setTranslationY(offset);

                    return insets;
                }
            });

            rootView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {

                    return null;
                }
            });
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            LinearLayout editTextContainer = findViewById(R.id.linearLayout3);

            final ValueAnimator anim = ValueAnimator.ofInt(recyclerView.getMeasuredHeight(), -100);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
                    layoutParams.height = val;
                    recyclerView.setLayoutParams(layoutParams);
                }
            });
            anim.setDuration(500);

            LayoutTransition layoutTransition = rootView.getLayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            layoutTransition.setStartDelay(LayoutTransition.CHANGING, 0);
            layoutTransition.setDuration(500);
            layoutTransition.addTransitionListener(new LayoutTransition.TransitionListener() {
                @Override
                public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                    //  anim.start();
                }

                @Override
                public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                    EditText messageView = findViewById(R.id.message_box);
                    messageView.setCursorVisible(true);
                    messageView.requestFocus();
                }
            });


            layoutTransition.setInterpolator(LayoutTransition.CHANGING, new DecelerateInterpolator(6.5f));

        }


        SliderActivity sliderActivity = new SliderActivity();
        sliderActivity.attachSlider(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public static void openActivity(Context context, int chatId, boolean isDialog, String name,
                                    String avatarPath) {
        Intent intent = new Intent(context, MessagingActivity.class);
        intent.putExtra(APIKeys.CHAT_ID, chatId);
        intent.putExtra("isd", isDialog);
        intent.putExtra(APIKeys.NAME, name);
        intent.putExtra(APIKeys.AVATAR, avatarPath);
        context.startActivity(intent);
    }

    public void back(View v) {
        onBackPressed();
    }

    private FilePickerBottomSheet filePickerBottomSheet;
    public void pickFiles(View v) {
        final EditText messageView = findViewById(R.id.message_box);

        filePickerBottomSheet = new FilePickerBottomSheet();
        filePickerBottomSheet.show(getSupportFragmentManager(), "blocked");
        filePickerBottomSheet.setRunnable(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                ImageSendActivity.open(MessagingActivity.this, filePickerBottomSheet.getImages().get(position), messageView.getText().toString());

            }
        });
    }


    public void showInfo(View v) {
        final ConversationBottomSheet conversationBottomSheet = new ConversationBottomSheet();
        conversationBottomSheet.setCid(String.valueOf(chatId));
        conversationBottomSheet.show(getSupportFragmentManager(), "blocked");
        conversationBottomSheet.setCancelable(true);
    }


    public boolean isrecording = false;


    public void registerSocket() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    messagesSocket = new MessagesSocket(CachedValues.getSessionKey(MessagingActivity.this), String.valueOf(chatId), new MessagesSocket.MessageReceiveHandler() {
                        @Override
                        public void onMessageReceive(final SendMessageResponse sendMessageResponse) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        if (!ids.containsKey(String.valueOf(sendMessageResponse.getMessage().getId()))) {
                                            messageList.add(sendMessageResponse.getMessage());
                                            adapter.notifyDataSetChanged();
                                        }
                                        ids.put(String.valueOf(sendMessageResponse.getMessage().getId()), sendMessageResponse.getMessage());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }
                    });

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Thread closeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    messagesSocket.getWebSocket().getUserSession().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        closeThread.start();

        ChatsListAdapter.isMessagingActivityOpened = true;
    }


    public boolean isRecordPanelShown = false;
    private Thread recordWaiter;

    private void loadChatInfo() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ChatInfoResponse chatInfoResponse = (new ChatInfoRequest(CachedValues.getSessionKey(getApplicationContext()), String.valueOf(chatId))).execute();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                TextView membersView = findViewById(R.id.subtitle);
                                membersView.setText(chatInfoResponse.getMembers().size() + " " + getResources().getString(R.string.members));
                            } catch (ResponseException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                } catch (ResponseException e) {
                    e.printStackTrace();
                } catch (NotCachedException e) {
                    e.printStackTrace();
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CuteToast.showError(e.getMessage(), MessagingActivity.this);

                        }
                    });
                }
            }
        });
        thread.start();
    }

    private void processListUpdate() {

        // Обработка ответа JSON
        Thread loadThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    GetMessageListResponse getMessageListResponse = (new GetMessageListRequest(CachedValues.getSessionKey(getApplicationContext()), String.valueOf(chatId))).execute();
                    if (getMessageListResponse.isSuccess()) {
                        List<Message> messages = getMessageListResponse.getMessages();
                        for (final Message message : messages) {


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!ids.containsKey(String.valueOf(message.getId()))) {

                                        ids.put(String.valueOf(message.getId()), message);

                                        adapter.addItem(message);

                                    } else {
                                        // Если сообщение уже есть проверяем не изменился ли статус прочитанности
                                    }
                                    LinearLayout loadingLayot = findViewById(R.id.loadingLayout);
                                    loadingLayot.setVisibility(View.INVISIBLE);
                                }
                            });
                            // Если это id уже есть, то проверяем прочитанность, а если нет, то добавляем

                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final LinearLayout error = findViewById(R.id.error);
                                error.setVisibility(View.VISIBLE);
                                errorPanel.show();
                            }
                        });
                    }


                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final LinearLayout error = findViewById(R.id.error);
                            error.setVisibility(View.VISIBLE);
                            errorPanel.show();
                        }
                    });
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        System.out.println("hmmm");
                        final RecyclerView recyclerView = findViewById(R.id.messages);
                        adapter = new MessagesAdapter(MessagingActivity.this, messageList, isDialog, recyclerView);
                        recyclerView.setAdapter(adapter);
                        findViewById(R.id.loadingLayout).setVisibility(View.INVISIBLE);
                    }
                });

            }
        });
        loadThread.start();


    }


    @SuppressLint("ClickableViewAccessibility")
    public void setupVoiceButton() {
        final ImageView microphone = findViewById(R.id.sendVoice);
        microphone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, final MotionEvent event) {
                final float max = 2.0f;
                final float pivotX = 0.5f;
                final float pivotY = 0.5f;
                final int duration = 150;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (recordWaiter != null) {
                            recordWaiter.interrupt();
                        }
                        recordWaiter = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(200);
                                    if (event.getAction() == MotionEvent.ACTION_DOWN | event.getAction() == MotionEvent.ACTION_MOVE) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                isrecording = true;
                                                SendorsControl.vibrate(getApplicationContext(), 100);
                                                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, max, 1.0f, max, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
                                                scaleAnimation.setDuration(duration);


                                                scaleAnimation.setFillAfter(true);

                                                microphone.startAnimation(scaleAnimation);
                                            }
                                        });
                                    } else {

                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        recordWaiter.start();

                        break;
                    case MotionEvent.ACTION_MOVE: // движение
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:

                        if (isrecording) {
                            ScaleAnimation dscaleAnimation = new ScaleAnimation(max, 1.0f, max, 1.0f, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
                            dscaleAnimation.setDuration(duration);
                            dscaleAnimation.setFillAfter(true);


                            microphone.startAnimation(dscaleAnimation);// отпускание
                            isrecording = false;

                        } else {
                            if (!isRecordPanelShown) {
                                final LinearLayout recordPanel = findViewById(R.id.recordPanel);
                                recordPanel.setVisibility(View.VISIBLE);
                                final Animation fadeIn = new AlphaAnimation(0, 1);

                                fadeIn.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        isRecordPanelShown = true;
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                fadeIn.setDuration(300);
                                Thread panelWaiter = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(1000);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    Animation fadeOut = new AlphaAnimation(1, 0);

                                                    fadeOut.setDuration(300);
                                                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                                                        @Override
                                                        public void onAnimationStart(Animation animation) {
                                                            System.out.println("FADEOUT!");
                                                        }

                                                        @Override
                                                        public void onAnimationEnd(Animation animation) {
                                                            recordPanel.setVisibility(View.INVISIBLE);
                                                            isRecordPanelShown = false;

                                                        }

                                                        @Override
                                                        public void onAnimationRepeat(Animation animation) {

                                                        }
                                                    });

                                                    if (recordPanel.getAnimation() != null) {
                                                        if (recordPanel.getAnimation().hasEnded()) {
                                                            if (recordPanel.getVisibility() != View.INVISIBLE) {
                                                                recordPanel.startAnimation(fadeOut);
                                                            }
                                                        }
                                                    } else {
                                                        if (recordPanel.getVisibility() != View.INVISIBLE) {
                                                            recordPanel.startAnimation(fadeOut);

                                                        }
                                                    }

                                                }

                                            });
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                if (recordPanel.getAnimation() != null) {
                                    if (recordPanel.getAnimation().hasEnded()) {
                                        recordPanel.startAnimation(fadeIn);
                                        panelWaiter.start();
                                    }
                                } else {
                                    recordPanel.startAnimation(fadeIn);
                                    panelWaiter.start();
                                }

                            }
                        }
                }
                return true;
            }
        });

    }

    public void setupOnTextInput() {
        final ImageView sendBtn = findViewById(R.id.sendButton);
        final ImageView voiceBtn = findViewById(R.id.sendVoice);
        final EditText editText = findViewById(R.id.message_box);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int duration = 150;
                float pivotX = 0.5f;
                float pivotY = 0.5f;
                ScaleAnimation decreaseAnimation = new ScaleAnimation(1.0f, 0, 1.0f, 0, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
                decreaseAnimation.setDuration(duration);
                decreaseAnimation.setFillAfter(true);

                ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1.0f, 0, 1.0f, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
                scaleAnimation.setDuration(duration);
                scaleAnimation.setFillAfter(true);
                if (String.valueOf(editText.getText()).equals("")) {
                    isVoice = true;
                    voiceBtn.startAnimation(scaleAnimation);
                    sendBtn.startAnimation(decreaseAnimation);
                    sendBtn.setEnabled(false);
                    voiceBtn.setEnabled(true);
                } else {
                    if (isVoice == true) {
                        isVoice = false;
                        sendBtn.setEnabled(true);
                        voiceBtn.setEnabled(false);
                        voiceBtn.startAnimation(decreaseAnimation);
                        sendBtn.startAnimation(scaleAnimation);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null) return;

        if (resultCode == ImageSendActivity.CODE) {
            if(filePickerBottomSheet != null)
            {
                filePickerBottomSheet.dismiss();
            }
            final String message = data.getStringExtra("text");
            String imageUri = data.getStringExtra("uri");
            System.out.println(imageUri);

           ImageFile imageFile;

            try {
                imageFile = new ImageFile(getRealPathFromURI(this, Uri.parse(imageUri)));
            }
            catch (Exception e)
            {
                imageFile = new ImageFile(imageUri);
            }


            final ImageFile finalImageFile = imageFile;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final UploadImageResponse uploadImageResponse = (new UploadImageRequest(finalImageFile, CachedValues.getSessionKey(getApplicationContext()))).execute();
                        if (!uploadImageResponse.isSuccess()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CuteToast.showError("Failed upload image!", MessagingActivity.this);
                                }

                            });
                        } else {

                            mediaIdToSend = uploadImageResponse.getMediaId();
                            mediaPathToSend = uploadImageResponse.getPath();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    sendMessageWithPreview(message);
                                }
                            });

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void sendMessageWithPreview(final String message) {
        final Message messagePreview;
        try {
            EditText messageView = findViewById(R.id.message_box);
            messageView.setText("");
            String placegolderText = message;
            if(mediaIdToSend != null)
            {
                placegolderText += "(Изображение)";
            }
            messagePreview = new Message(-1, 1, false, placegolderText, null, null, null, null, null, null, null,
                    new User(null, null, null, null, CachedValues.getId(this), null, null, null, false));
            adapter.addItem(messagePreview);


            Thread sendThread = new Thread(new Runnable() {
                @Override
                public void run() {


                    try {

                        final SendMessageResponse sendMessageResponse;
                        if (mediaIdToSend == null) {
                            sendMessageResponse = (new SendMessageRequest(CachedValues.getSessionKey(getApplicationContext()), String.valueOf(chatId), message)).execute();
                        } else {
                            sendMessageResponse = (new SendMessageRequest(CachedValues.getSessionKey(getApplicationContext()), String.valueOf(chatId), message, mediaIdToSend)).execute();
                        }
                        mediaIdToSend = null;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {


                                    Message newMessage = sendMessageResponse.getMessage();
                                    messagePreview.setId(newMessage.getId());
                                    messagePreview.setTime(newMessage.getTime());
                                    messagePreview.setText(newMessage.getText());
                                    messagePreview.setType(newMessage.getType());
                                    messagePreview.setAttachmentData(newMessage.getAttachmentData());
                                    messagePreview.setAttachmentPath(newMessage.getCleanAttachmentPath());
                                    messagePreview.setAttachmentType(newMessage.getAttachmentType());
                                    try {

                                        messagePreview.notifyDataChanged();
                                    } catch (Exception ignored) {
                                    }

                                    ids.put(String.valueOf((sendMessageResponse.getMessage().getId())), messagePreview);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            messagePreview.setId(-2);
                                            messagePreview.notifyDataChanged();
                                        }
                                    });
                                }

                            }
                        });
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messagePreview.setId(-2);
                                messagePreview.notifyDataChanged();
                            }
                        });
                        e.printStackTrace();
                    }

                }
            });
            sendThread.start();
        } catch (NotCachedException e) {

            e.printStackTrace();
        }
    }

    public void sendMessage(View view) {

        EditText messageView = findViewById(R.id.message_box);

        final String message = String.valueOf(messageView.getText());
        messageView.setText("");

        sendMessageWithPreview(message);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}