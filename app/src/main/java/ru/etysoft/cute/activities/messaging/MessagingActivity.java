package ru.etysoft.cute.activities.messaging;

import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.WindowInsetsAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.math.MathUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.Profile;
import ru.etysoft.cute.activities.chatslist.ChatsListAdapter;
import ru.etysoft.cute.activities.ImageSend.ImageSendActivity;
import ru.etysoft.cute.activities.messaging.messages.BeginningView;
import ru.etysoft.cute.activities.messaging.messages.MessageObject;
import ru.etysoft.cute.activities.messaging.messages.RecyclerObject;
import ru.etysoft.cute.activities.messaging.messages.MessagesAdapter;
import ru.etysoft.cute.bottomsheets.conversation.ConversationBottomSheet;
import ru.etysoft.cute.bottomsheets.filepicker.FilePickerBottomSheet;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.components.CuteToast;
import ru.etysoft.cute.components.ErrorPanel;
import ru.etysoft.cute.components.ForwardedMessage;
import ru.etysoft.cute.components.InfoPanel;
import ru.etysoft.cute.components.SmartImageView;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.MessageNotFoundException;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.lang.StringsRepository;
import ru.etysoft.cute.transition.Transitions;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cute.utils.NetworkStateReceiver;
import ru.etysoft.cute.utils.SendorsControl;
import ru.etysoft.cute.utils.SliderActivity;
import ru.etysoft.cute.utils.SocketHolder;
import ru.etysoft.cuteframework.data.APIKeys;
import ru.etysoft.cuteframework.methods.chat.Chat;
import ru.etysoft.cuteframework.methods.chat.GetHistory.GetMessageListRequest;
import ru.etysoft.cuteframework.methods.chat.GetHistory.GetMessageListResponse;
import ru.etysoft.cuteframework.methods.chat.SendMessage.SendMessageRequest;
import ru.etysoft.cuteframework.methods.chat.SendMessage.SendMessageResponse;
import ru.etysoft.cuteframework.methods.media.UploadImageRequest;
import ru.etysoft.cuteframework.methods.media.UploadImageResponse;
import ru.etysoft.cuteframework.methods.messages.Message;
import ru.etysoft.cuteframework.methods.user.User;
import ru.etysoft.cuteframework.requests.attachements.ImageFile;
import ru.etysoft.cuteframework.sockets.events.MemberStateChangedEvent;

public class MessagingActivity extends AppCompatActivity implements ConversationBottomSheet.BottomSheetListener, MessagingContract.View {

    public final Map<String, Message> loadedMessagesIds = new HashMap<String, Message>();
    private MessagesAdapter adapter;

    public long chatId = 1;
    private long accountId = -1;
    private String name = "42";
    private String mediaIdToSend;
    public String forwardedMessageId = null;

    public boolean isVoiceButtonShowed = true;
    private boolean isRecordingVoiceMessage = false;

    private LinearLayout infoContainer;
    private LinearLayout errorContainer;
    private ErrorPanel errorPanel;
    private InfoPanel infoPanel;
    private TextView statusView;
    private TextView titleView;
    private Avatar avatarView;
    private NetworkStateReceiver networkStateReceiver;
    private ForwardedMessage forwardedMessagePreview;
    private LinearLayout forwardedMessageContainer;
    private RecyclerView messageRecyclerView;
    private ViewGroup rootView;
    private List<Long> readIds;

    private MessagingContract.Presenter presenter;

    private Runnable onResume;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        chatId = getIntent().getLongExtra(APIKeys.CHAT_ID, 0);
        name = getIntent().getStringExtra(APIKeys.NAME);
        String type = getIntent().getStringExtra(APIKeys.TYPE);
        String avatar = getIntent().getStringExtra(APIKeys.AVATAR);

        try {
            presenter = new MessagingPresenter(this, type, avatar, CachedValues.getSessionKey(this), String.valueOf(chatId));
        } catch (NotCachedException e) {
            e.printStackTrace();
            finish();
        }

        presenter.registerChatSocket();

        initViews();


        networkStateReceiver = new NetworkStateReceiver(new Runnable() {
            @Override
            public void run() {
                presenter.registerChatSocket();
            }
        }, new Runnable() {
            @Override
            public void run() {
            }
        });

        networkStateReceiver.register(this);

        setAvatarImage(avatar);
        avatarView.setAcronym((name), Avatar.Size.SMALL);

        if (!presenter.isDialog()) {
            presenter.loadChatInfo();
            avatarView.generateIdPicture(chatId);
        } else {
            accountId = getIntent().getLongExtra(APIKeys.ACCOUNT_ID, -1L);
            avatarView.generateIdPicture(accountId);

        }

        errorPanel.setReloadAction(new Runnable() {
            @Override
            public void run() {
                errorPanel.hide(new Runnable() {
                    @Override
                    public void run() {
                        processListUpdate();
                        errorContainer.setVisibility(View.INVISIBLE);

                    }
                });
            }
        });

        titleView.setText(name);

        setupVoiceButton();
        setupMessageInputHandler();

        processListUpdate();


        SliderActivity sliderActivity = new SliderActivity();
        sliderActivity.attachSlider(this);

        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);

    }

    @Override
    public void initViews() {
        rootView = findViewById(R.id.rootView);
        avatarView = findViewById(R.id.avatar);
        forwardedMessagePreview = findViewById(R.id.forwardedMessagePreview);
        forwardedMessageContainer = findViewById(R.id.fwdContainer);
        forwardedMessagePreview.initComponent(false);
        errorPanel = findViewById(R.id.error_panel);
        infoPanel = findViewById(R.id.info_panel);
        errorPanel.getRootView().setVisibility(View.INVISIBLE);
        infoPanel.getRootView().setVisibility(View.INVISIBLE);
        infoContainer = findViewById(R.id.info);
        infoContainer.setVisibility(View.INVISIBLE);
        errorContainer = findViewById(R.id.error);
        errorContainer.setVisibility(View.INVISIBLE);
        titleView = findViewById(R.id.title);
        statusView = findViewById(R.id.subtitle);
        setupRecyclerView();
    }

    @Override
    public String getStatusText() {
        return String.valueOf(statusView.getText());
    }

    @Override
    public String getChatName() {
        return String.valueOf(titleView.getText());
    }

    @Override
    public String getAccountId() {
        try {
            return CachedValues.getId(this);
        } catch (NotCachedException e) {
            e.printStackTrace();
            return "-1";
        }
    }

    @Override
    public String getStringsRepositoryResult(int resId) {
        return StringsRepository.getOrDefault(R.string.offline, MessagingActivity.this);
    }

    @Override
    public void setupRecyclerView() {
        messageRecyclerView = findViewById(R.id.messages);
        adapter = new MessagesAdapter(this, new ArrayList<RecyclerObject>(), presenter.isDialog(), messageRecyclerView,
                (LinearLayout) MessagingActivity.this.findViewById(R.id.bottom_scroll_button),
                (TextView) MessagingActivity.this.findViewById(R.id.timeView));
        messageRecyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback swipeForForwardMessageCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public float getSwipeEscapeVelocity(float defaultValue) {
                return 2.0f;
            }

            @Override
            public float getSwipeVelocityThreshold(float defaultValue) {
                return 0.1f;
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0.1f;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                RecyclerObject recyclerObject = adapter.getMessagesList().get(viewHolder.getAdapterPosition());
                if (recyclerObject instanceof MessageObject) {
                    Message message = ((MessageObject) recyclerObject).getMessage();
                    if (message.getType() != null && message.getType().equals(Message.Type.USER) && dX > -(recyclerView.getWidth() / 4)) {
                        super.onChildDraw(c, recyclerView, viewHolder, dX / 2, dY, actionState, isCurrentlyActive);
                    }
                }
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                RecyclerObject recyclerObject = adapter.getMessagesList().get(viewHolder.getAdapterPosition());
                if (recyclerObject instanceof MessageObject) {
                    Message message = ((MessageObject) recyclerObject).getMessage();
                    if (message.getType().equals(Message.Type.USER)) {
                        adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        forwardedMessageId = String.valueOf(message.getId());
                        forwardedMessageContainer.setVisibility(View.VISIBLE);
                        forwardedMessagePreview.setContent(message, MessagingActivity.this);
                        SendorsControl.vibrate(MessagingActivity.this, 50);
                    }

                }
            }
        };

        new ItemTouchHelper(swipeForForwardMessageCallback).attachToRecyclerView(messageRecyclerView);

        /*
          Keyboard appear animation

          In Android API >= 30 we can animate layout resize in realtime
          At API < 30 we emulate layout resizing after keyboard appearing
         */
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

            final ValueAnimator anim = ValueAnimator.ofInt(messageRecyclerView.getMeasuredHeight(), -100);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = messageRecyclerView.getLayoutParams();
                    layoutParams.height = val;
                    messageRecyclerView.setLayoutParams(layoutParams);
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
    }

    @Override
    public void showErrorToast(String message) {
        CuteToast.showError(message, this);
    }

    @Override
    public void addMessage(Message message) {
        adapter.addItem(new MessageObject(messageRecyclerView, message), true);
    }

    @Override
    public Map<String, Message> getMessagesIds() {
        return loadedMessagesIds;
    }

    public void clearForwardMessage() {
        forwardedMessageId = "";
        forwardedMessageContainer.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (onResume != null) {
            onResume.run();
        }
    }

    public static void openActivityForChat(Context context, long chatId, String name,
                                           String avatarPath) {
        Intent intent = new Intent(context, MessagingActivity.class);
        intent.putExtra(APIKeys.CHAT_ID, chatId);
        intent.putExtra(APIKeys.TYPE, Chat.Types.CONVERSATION);
        intent.putExtra(APIKeys.NAME, name);
        intent.putExtra(APIKeys.AVATAR, avatarPath);
        context.startActivity(intent);
    }

    public static void openActivityForDialog(Context context, long chatId, long accountId, String name,
                                             String avatarPath) {
        Intent intent = new Intent(context, MessagingActivity.class);
        intent.putExtra(APIKeys.CHAT_ID, chatId);
        intent.putExtra(APIKeys.TYPE, Chat.Types.PRIVATE);
        intent.putExtra(APIKeys.NAME, name);
        intent.putExtra(APIKeys.ACCOUNT_ID, accountId);
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
        filePickerBottomSheet.setRunnable(new FilePickerBottomSheet.ItemClickListener() {
            @Override
            public void onItemClick(int pos, View view) {
                ImageSendActivity.open(MessagingActivity.this, filePickerBottomSheet.getImages().get(pos), messageView.getText().toString(),
                        (SmartImageView) view);
            }
        });
    }


    @Override
    public void showEmptyChatPanel(View v) {
        if (presenter.isDialog()) {
            Intent intent = new Intent(MessagingActivity.this, Profile.class);
            intent.putExtra("id", accountId);
            intent.putExtra(Profile.ALLOW_OPEN_CHAT, false);
            if (presenter.getAvatarUrl() != null) {
                intent.putExtra(Profile.AVATAR, presenter.getAvatarUrl());
            }

            Bundle bundle = Transitions.makeOneViewTransition(avatarView, MessagingActivity.this, intent, getResources().getString(R.string.transition_profile));
            if (bundle == null) {
                startActivity(intent);
            } else {
                startActivity(intent, bundle);
            }
        } else {
            final ConversationBottomSheet conversationBottomSheet = new ConversationBottomSheet();
            conversationBottomSheet.setCid(String.valueOf(chatId));
            conversationBottomSheet.show(getSupportFragmentManager(), "blocked");
            conversationBottomSheet.setCancelable(true);
        }
    }

    @Override
    public void setHeaderInfo(String avatarUrl, String title, String status) {
        setAvatarImage(avatarUrl);
        setChatName(title);
        setStatus(status);
    }

    @Override
    public void setStatus(String status) {
        statusView.setText(status);
    }

    @Override
    public void setChatName(String chatName) {
        titleView.setText(chatName);
    }

    @Override
    public void setAvatarImage(String avatarUrl) {
        if (avatarUrl != null) {
            Picasso.get().load(avatarUrl).transform(new CircleTransform()).into(avatarView.getPictureView());
        }
    }


    public void setOnResume(Runnable runnable) {
        onResume = runnable;
    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChatsListAdapter.isMessagingActivityOpened = true;
    }


    public boolean isRecordPanelShown = false;
    private Thread recordWaiter;


    private void processListUpdate() {

        // Обработка ответа JSON
        Thread loadThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    final GetMessageListResponse getMessageListResponse = (new GetMessageListRequest(CachedValues.getSessionKey(getApplicationContext()), String.valueOf(chatId))).execute();
                    if (getMessageListResponse.isSuccess()) {
                        List<Message> messages = getMessageListResponse.getMessages();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setFirstMessageId(getMessageListResponse.getFirstMessageId());
                            }
                        });
                        boolean addClouds = false;
                        for (final Message message : messages) {


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!loadedMessagesIds.containsKey(String.valueOf(message.getId()))) {

                                        loadedMessagesIds.put(String.valueOf(message.getId()), message);

                                        adapter.addItem(new MessageObject(messageRecyclerView, message), true);

                                    } else {
                                        // Если сообщение уже есть проверяем не изменился ли статус прочитанности
                                    }
                                    LinearLayout loadingLayout = findViewById(R.id.loadingLayout);
                                    loadingLayout.setVisibility(View.INVISIBLE);
                                }
                            });
                            // Если это id уже есть, то проверяем прочитанность, а если нет, то добавляем
                            if(message.getId() == getMessageListResponse.getFirstMessageId())
                            {
                                addClouds = true;
                            }
                        }

                        if(addClouds)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                        adapter.addItem(new BeginningView(messageRecyclerView));


                                }
                            });
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

                        if (loadedMessagesIds.size() == 0) {
                            final LinearLayout info = findViewById(R.id.info);
                            info.setVisibility(View.VISIBLE);
                            infoPanel.setVisibility(View.VISIBLE);
                            infoPanel.show();
                        } else {
                            final LinearLayout info = findViewById(R.id.info);
                            info.setVisibility(View.INVISIBLE);
                            infoPanel.setVisibility(View.INVISIBLE);
                        }
                        adapter.notifyDataSetChanged();
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

                        recordWaiter = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(200);
                                    if (event.getAction() == MotionEvent.ACTION_DOWN | event.getAction() == MotionEvent.ACTION_MOVE) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                isRecordingVoiceMessage = true;
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
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:

                        if (isRecordingVoiceMessage) {
                            ScaleAnimation dscaleAnimation = new ScaleAnimation(max, 1.0f, max, 1.0f, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
                            dscaleAnimation.setDuration(duration);
                            dscaleAnimation.setFillAfter(true);


                            microphone.startAnimation(dscaleAnimation);
                            isRecordingVoiceMessage = false;

                        } else {
                            if (!isRecordPanelShown) {
                                final LinearLayout recordPanel = findViewById(R.id.recordPanel);
                                recordPanel.setVisibility(View.VISIBLE);
                                final Animation fadeIn = new AlphaAnimation(0, 1);
                                fadeIn.setFillAfter(true);

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
                                                    fadeOut.setFillAfter(true);

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

    public void setMessageRead(final long messageId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    adapter.getMessage(adapter.getPositionById(messageId)).setRead(true);
                    adapter.notifyItemChanged(adapter.getPositionById(messageId));
                } catch (MessageNotFoundException ignored) {

                }


            }
        });

    }

    private long lastTypingTime = 0;
    private boolean isTyping = false;


    public void setupMessageInputHandler() {
        final ImageView sendBtn = findViewById(R.id.sendButton);
        final ImageView voiceBtn = findViewById(R.id.sendVoice);
        final EditText editText = findViewById(R.id.message_box);
        Thread typingWaiter = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {

                        Thread.sleep(1000);
                        if (System.currentTimeMillis() > lastTypingTime + 1000 && isTyping) {
                            isTyping = false;
                            SocketHolder.getChatSocket().sendRequest(chatId, MemberStateChangedEvent.States.ONLINE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        typingWaiter.start();
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

                lastTypingTime = System.currentTimeMillis();

                if (!isTyping) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                SocketHolder.getChatSocket().sendRequest(chatId, MemberStateChangedEvent.States.TYPING);


                            } catch (Exception e) {
                                try {
                                    SocketHolder.initialize(CachedValues.getSessionKey(MessagingActivity.this));
                                } catch (Exception ignored) {

                                }
                            }
                        }
                    });
                    thread.start();

                    isTyping = true;
                }

                ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1.0f, 0, 1.0f, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
                scaleAnimation.setDuration(duration);
                scaleAnimation.setFillAfter(true);
                if (String.valueOf(editText.getText()).equals("")) {
                    isVoiceButtonShowed = true;
                    voiceBtn.startAnimation(scaleAnimation);
                    sendBtn.startAnimation(decreaseAnimation);
                    sendBtn.setEnabled(false);
                    voiceBtn.setEnabled(true);
                } else {
                    if (isVoiceButtonShowed) {
                        isVoiceButtonShowed = false;
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
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void scrollToBottom(View v) {
        adapter.getRecyclerView().smoothScrollToPosition(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null) return;

        if (resultCode == ImageSendActivity.CODE) {
            if (filePickerBottomSheet != null) {

                /**
                 * Throws an java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
                 * on some devices (tested on Android 5.1)
                 */
                try {
                    filePickerBottomSheet.dismiss();
                } catch (Exception ignored) {
                }
            }
            final String message = data.getStringExtra("text");
            String imageUri = data.getStringExtra("uri");


            ImageFile imageFile;

            try {
                imageFile = new ImageFile(getRealPathFromURI(this, Uri.parse(imageUri)));
            } catch (Exception e) {
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

    public void clearFwd(View v) {
        clearForwardMessage();
    }

    private long imaginaryMessageId = -2;

    public void sendMessageWithPreview(final String message) {

        final LinearLayout info = findViewById(R.id.info);
        info.setVisibility(View.INVISIBLE);
        infoPanel.setVisibility(View.INVISIBLE);
        final Message messagePreview;
        try {
            final long localId = imaginaryMessageId - 1;
            EditText messageView = findViewById(R.id.message_box);
            messageView.setText("");
            String placegolderText = message;
            if (mediaIdToSend != null) {
                placegolderText += "(Изображение)";
            }
            messagePreview = new Message((int) localId, 1, false, placegolderText, null, null, null, null, null, null, null,
                    new User(null, null, null, null, CachedValues.getId(this), null, null, null, false));
            MessageObject messageObject = new MessageObject(messageRecyclerView, messagePreview);
            adapter.addItem(messageObject, true);
            adapter.getRecyclerView().scrollToPosition(adapter.getMessagesList().indexOf(messageObject));

            Thread sendThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    isTyping = false;
                    try {
                        SocketHolder.getChatSocket().sendRequest(chatId, MemberStateChangedEvent.States.ONLINE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {

                        final SendMessageResponse sendMessageResponse;
                        if (mediaIdToSend == null) {
                            sendMessageResponse = (new SendMessageRequest(CachedValues.getSessionKey(getApplicationContext()), String.valueOf(chatId), message, forwardedMessageId)).execute();
                        } else {
                            sendMessageResponse = (new SendMessageRequest(CachedValues.getSessionKey(getApplicationContext()), String.valueOf(chatId), message, mediaIdToSend, forwardedMessageId)).execute();
                        }

                        mediaIdToSend = null;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clearForwardMessage();
                                try {


                                    Message newMessage = sendMessageResponse.getMessage();
                                    messagePreview.setId(newMessage.getId());
                                    messagePreview.setTime(newMessage.getTime());
                                    messagePreview.setText(newMessage.getText());
                                    messagePreview.setType(newMessage.getType());
                                    messagePreview.setSender(newMessage.getSender());
                                    messagePreview.setAttachmentData(newMessage.getAttachmentData());
                                    messagePreview.setAttachmentPath(newMessage.getCleanAttachmentPath());
                                    messagePreview.setAttachmentType(newMessage.getAttachmentType());
                                    messagePreview.setForwardedMessage(newMessage.getForwardedMessage());
                                    try {
                                        int position = adapter.getPositionList().get(localId);
                                        adapter.getPositionList().remove(localId);
                                        adapter.getPositionList().put((long) newMessage.getId(), position);
                                        messagePreview.notifyDataChanged();
                                    } catch (Exception ignored) {
                                    }

                                    if (!loadedMessagesIds.containsKey(String.valueOf(sendMessageResponse.getMessage().getId()))) {
                                        loadedMessagesIds.put(String.valueOf((sendMessageResponse.getMessage().getId())), messagePreview);
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();

                                    messagePreview.setId(-2);
                                    messagePreview.notifyDataChanged();

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

    @Override
    public void onSendMessageButtonClick(View v) {
        EditText messageView = findViewById(R.id.message_box);
        final String message = String.valueOf(messageView.getText());
        messageView.setText("");
        sendMessageWithPreview(message);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }


}