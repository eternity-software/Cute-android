package ru.etysoft.cute.activities.messaging;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.ImageSend.ImageSendActivity;
import ru.etysoft.cute.activities.Profile;
import ru.etysoft.cute.activities.chatslist.ChatsListAdapter;
import ru.etysoft.cute.bottomsheets.conversation.ConversationBottomSheet;
import ru.etysoft.cute.bottomsheets.filepicker.FilePickerBottomSheet;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.components.CuteToast;
import ru.etysoft.cute.components.ErrorPanel;
import ru.etysoft.cute.components.FilePreview;
import ru.etysoft.cute.components.ForwardedMessage;
import ru.etysoft.cute.components.InfoPanel;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.lang.CustomLanguage;
import ru.etysoft.cute.resizer.FluidContentResizer;
import ru.etysoft.cute.themes.Theme;
import ru.etysoft.cute.transition.Transitions;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cute.utils.NetworkStateReceiver;
import ru.etysoft.cute.utils.SendorsControl;
import ru.etysoft.cute.utils.SliderActivity;
import ru.etysoft.cuteframework.consts.APIKeys;
import ru.etysoft.cuteframework.methods.chat.ChatSendMessageRequest;
import ru.etysoft.cuteframework.models.Chat;
import ru.etysoft.cuteframework.models.messages.ChatCreatedData;
import ru.etysoft.cuteframework.models.messages.Message;
import ru.etysoft.cuteframework.models.messages.ServiceMessage;
import ru.etysoft.cuteframework.requests.attachements.ImageFile;

public class MessagingActivity extends AppCompatActivity implements ConversationBottomSheet.BottomSheetListener, MessagingContract.View {

    public final Map<String, Message> loadedMessagesIds = new HashMap<String, Message>();

    public String chatId = "";
    private long accountId = -1;
    private String name = "42";
    public String forwardedMessageId = null;

    public boolean isVoiceButtonShowed = true;

    private boolean isRecordingVoiceMessage = false;

    private LinearLayout infoContainer;
    private LinearLayout errorContainer;
    private ErrorPanel errorPanel;
    private InfoPanel infoPanel;
    private TextView statusView;
    private long lastTypingTime = 0;
    private boolean isTyping = false;
    private MessagesAdapter adapter;

    private TextView titleView;
    private Avatar avatarView;
    private NetworkStateReceiver networkStateReceiver;
    private ForwardedMessage forwardedMessagePreview;
    private LinearLayout forwardedMessageContainer;
    private RecyclerView messageRecyclerView;
    private ViewGroup rootView;

    private MessagingContract.Presenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        chatId = getIntent().getStringExtra(APIKeys.Chat.CHAT_ID);
        name = getIntent().getStringExtra(APIKeys.Chat.NAME);
        String type = getIntent().getStringExtra(APIKeys.Chat.TYPE);
//        String avatar = getIntent().getStringExtra(APIKeys.AVATAR);

        Theme.applyBackground(findViewById(R.id.rootView));


        presenter = new MessagingPresenter(this, type, null, chatId);

        // TODO: socket support
        // presenter.registerChatSocket();

        initViews();


        networkStateReceiver = new NetworkStateReceiver(new Runnable() {
            @Override
            public void run() {


            }
        }, new Runnable() {
            @Override
            public void run() {
            }
        });

        networkStateReceiver.register(this);

        //setAvatarImage(avatar);
        avatarView.setAcronym((name), Avatar.Size.SMALL);

        if (!presenter.isDialog()) {
            presenter.loadChatInfo();
            avatarView.generateIdPicture(5);
        } else {

            avatarView.generateIdPicture(accountId);

        }

        errorPanel.setReloadAction(new Runnable() {
            @Override
            public void run() {
                errorPanel.hide(new Runnable() {
                    @Override
                    public void run() {
                        errorContainer.setVisibility(View.INVISIBLE);
                        presenter.loadLatestMessages();

                    }
                });
            }
        });

        titleView.setText(name);


        setupVoiceButton();
        setupMessageInputHandler();

        FluidContentResizer.INSTANCE.listen(this);

        SliderActivity sliderActivity = new SliderActivity();
        sliderActivity.attachSlider(this);

        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
        presenter.loadLatestMessages();

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
        Theme.applyBackground(rootView);
        Theme.applyThemeToActivity(this);
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
    public void loadMessages(List<MessageComponent> messageComponents) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.addAll( messageComponents);
                messageRecyclerView.scrollToPosition(0);

            }
        });
    }

    @Override
    public void loadPreviousMessages(List<MessageComponent> messageComponents) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.addAll( messageComponents);

            }
        });
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
    public MessagesAdapter getMessagesAdapter() {
        return adapter;
    }


    @Override
    public String getStringsRepositoryResult(int resId) {
        return CustomLanguage.getStringsRepository().getOrDefault(R.string.offline, MessagingActivity.this);
    }

    @Override
    public void setMessageRead(long messageId) {

    }

    @Override
    public void setupRecyclerView() {
        messageRecyclerView = findViewById(R.id.messages);
        adapter = new MessagesAdapter(this, presenter.getChatType());
        messageRecyclerView.setAdapter(adapter);


        messageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                final int offset = recyclerView.computeVerticalScrollOffset();
                final int extent = recyclerView.computeVerticalScrollExtent();
                final int bottomRange = recyclerView.computeVerticalScrollRange() -extent - offset;
                super.onScrolled(recyclerView, dx, dy);




                Runnable runnable =  new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int positionIndex= ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                                View startView = recyclerView.getChildAt(0);
                                int topView = (startView == null) ? 0 : (startView.getTop() - recyclerView.getPaddingTop());
                                if (positionIndex!= -1) {
                                 //   ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(positionIndex, topView);
                                }
                            }
                        });
                    }
                };
                if (offset < recyclerView.getHeight()) {
                    if (adapter.getMessageComponent(0).getMessage() instanceof ServiceMessage) {
                        if (((ServiceMessage) adapter.getMessageComponent(0).getMessage()).getServiceData() instanceof ChatCreatedData) {
                        } else {
                            presenter.loadUpperMessages(runnable);
                        }

                    }
                    else
                    {
                        System.out.println("============== ");
                        presenter.loadUpperMessages(runnable);
                    }

                }


            }
        });


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
//                RecyclerObject recyclerObject = adapter.getMessagesList().get(viewHolder.getAdapterPosition());
//                if (recyclerObject instanceof MessageObject) {
//                    Message message = ((MessageObject) recyclerObject).getMessage();
//                    if (message.getType() != null && message.getType().equals(Message.Type.USER) && dX > -(recyclerView.getWidth() / 4)) {
//                        super.onChildDraw(c, recyclerView, viewHolder, dX / 2, dY, actionState, isCurrentlyActive);
//                    }
//                }
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

//                RecyclerObject recyclerObject = adapter.getMessagesList().get(viewHolder.getAdapterPosition());
//                if (recyclerObject instanceof MessageObject) {
//                    Message message = ((MessageObject) recyclerObject).getMessage();
//                    if (message.getType().equals(Message.Type.USER)) {
//                        adapter.notifyItemChanged(viewHolder.getAdapterPosition());
//                        forwardedMessageId = String.valueOf(message.getId());
//                        forwardedMessageContainer.setVisibility(View.VISIBLE);
//                        forwardedMessagePreview.setContent(message, MessagingActivity.this);
//                        SendorsControl.vibrate(MessagingActivity.this, 50);
//                    }
//
//                }
            }
        };

        new ItemTouchHelper(swipeForForwardMessageCallback).attachToRecyclerView(messageRecyclerView);

    }

    @Override
    public void showErrorToast(String message) {
        CuteToast.showError(message, this);
    }

    @Override
    public void showErrorPanel() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                errorContainer.setVisibility(View.VISIBLE);
                errorPanel.setVisibility(View.VISIBLE);
                errorPanel.show();
            }
        });

    }

    @Override
    public void showInfoPanel() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final LinearLayout info = findViewById(R.id.info);
                info.setVisibility(View.VISIBLE);
                infoPanel.setVisibility(View.VISIBLE);
                infoPanel.show();
            }
        });

    }

    @Override
    public void hideInfoPanel() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final LinearLayout info = findViewById(R.id.info);
                info.setVisibility(View.INVISIBLE);
                infoPanel.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public void hideErrorPanel() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                errorPanel.hide(new Runnable() {
                    @Override
                    public void run() {
                        errorContainer.setVisibility(View.GONE);
                        errorPanel.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }


    @Override
    public Map<String, Message> getMessagesIds() {
        return loadedMessagesIds;
    }

    public void clearForwardMessage() {
        forwardedMessageId = "";
        forwardedMessageContainer.setVisibility(View.GONE);
    }
    

    public static void openActivityForChat(Context context, String chatId, String name) {
        Intent intent = new Intent(context, MessagingActivity.class);
        intent.putExtra(APIKeys.Chat.CHAT_ID, chatId);
        intent.putExtra(APIKeys.Chat.NAME, name);
        intent.putExtra(APIKeys.Chat.TYPE, Chat.TYPE_CONVERSATION);

        context.startActivity(intent);
    }

    public static void openActivityForDialog(Context context, String chatId) {
        Intent intent = new Intent(context, MessagingActivity.class);
//        intent.putExtra(APIKeys.CHAT_ID, chatId);
//        intent.putExtra(APIKeys.TYPE, Chat.Types.PRIVATE);
//        intent.putExtra(APIKeys.NAME, name);
//        intent.putExtra(APIKeys.ACCOUNT_ID, accountId);
//        intent.putExtra(APIKeys.AVATAR, avatarPath);
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
            public void onItemClick(int pos, final View view) {
                ImageSendActivity.open(MessagingActivity.this, filePickerBottomSheet.getMedia().get(pos).getFilePath(), messageView.getText().toString(),
                        (FilePreview) view);


            }
        });
    }


    @Override
    public void showChatInfo(View v) {
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

    @Override
    public void showLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout loadingLayout = findViewById(R.id.loadingLayout);
                loadingLayout.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void hideLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout loadingLayout = findViewById(R.id.loadingLayout);
                loadingLayout.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public void notifyDataSetChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
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
                            //SocketHolder.getChatSocket().sendRequest(chatId, MemberStateChangedEvent.States.ONLINE);
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

                                // SocketHolder.getChatSocket().sendRequest(chatId, MemberStateChangedEvent.States.TYPING);


                            } catch (Exception e) {
                                try {
                                    //  SocketHolder.initialize(CachedValues.getSessionKey(MessagingActivity.this));
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
            // TODO: Upload image to server
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void clearForwardButtonClick(View v) {
        clearForwardMessage();
    }


    public void scrollToBottom(View v)
    {
        messageRecyclerView.scrollToPosition(0);
    }

    public void sendMessageWithPreview(final String messageText) {

        MessageComponent messageComponent = new MessageComponent(MessageComponent.TYPE_MY_MESSAGE);

        messageComponent.setState(MessageComponent.STATE_PENDING);
        messageComponent.setPlaceholderText(messageText);
        adapter.sendMessagePending(messageComponent);

        final int offset = messageRecyclerView.computeVerticalScrollOffset();
        final int extent = messageRecyclerView.computeVerticalScrollExtent();
        final int bottomRange = messageRecyclerView.computeVerticalScrollRange() - extent - offset;

        if (bottomRange < messageRecyclerView.getHeight()) {
            messageRecyclerView.scrollToPosition(0);
        }

        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ChatSendMessageRequest.ChatSendMessageResponse chatSendMessageResponse = new ChatSendMessageRequest(chatId, messageText).execute();
                    if (chatSendMessageResponse.isSuccess()) {
                         messageComponent.setState(MessageComponent.STATE_SENT);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyItemChanged(adapter.getMessageComponentId(messageComponent));
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        sendThread.start();
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