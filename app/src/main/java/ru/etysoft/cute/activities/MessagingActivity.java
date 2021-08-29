package ru.etysoft.cute.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.chatslist.ChatsListAdapter;
import ru.etysoft.cute.activities.messages.MessageInfo;
import ru.etysoft.cute.activities.messages.MessagesAdapter;
import ru.etysoft.cute.bottomsheets.conversation.ConversationBottomSheet;
import ru.etysoft.cute.bottomsheets.filepicker.FilePickerBottomSheet;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.components.CuteToast;
import ru.etysoft.cute.components.ErrorPanel;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.lang.StringsRepository;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cute.utils.Numbers;
import ru.etysoft.cute.utils.SendorsControl;
import ru.etysoft.cute.utils.SliderActivity;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.chat.ServiceData;
import ru.etysoft.cuteframework.methods.media.UploadImageRequest;
import ru.etysoft.cuteframework.methods.media.UploadImageResponse;
import ru.etysoft.cuteframework.methods.messages.GetList.GetMessageListRequest;
import ru.etysoft.cuteframework.methods.messages.GetList.GetMessageListResponse;
import ru.etysoft.cuteframework.methods.messages.Message;
import ru.etysoft.cuteframework.methods.messages.Send.SendMessageRequest;
import ru.etysoft.cuteframework.methods.messages.Send.SendMessageResponse;
import ru.etysoft.cuteframework.requests.attachements.ImageFile;

public class MessagingActivity extends AppCompatActivity implements ConversationBottomSheet.BottomSheetListener {

    private List<MessageInfo> convInfos = new ArrayList<>();
    private Map<String, MessageInfo> ids = new HashMap<String, MessageInfo>();
    private MessagesAdapter adapter;

    private int cid = 1;
    private String name = "42";
    private String avatar = null;
    private String countMembers = "42";
    private boolean isDialog = false;
    private int ts = 0;
    private Thread waiter;
    private boolean closed = false;
    private ErrorPanel errorPanel;
    public boolean isVoice = true;

    private boolean isEmpty = true;
    private String mediaIdToSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        cid = getIntent().getIntExtra("cid", 0);
        name = getIntent().getStringExtra("name");
        avatar = getIntent().getStringExtra("cover");
        isDialog = getIntent().getBooleanExtra("isd", false);

        Avatar picture = findViewById(R.id.avatar);
        picture.generateIdPicture(cid);

        if(avatar != null)
        {
            Picasso.get().load(avatar).transform(new CircleTransform()).into(picture.getPictureView());
        }

        picture.setAcronym((String.valueOf(name.charAt(0))), Avatar.Size.SMALL);

        final ListView listView = findViewById(R.id.messages);
        adapter = new MessagesAdapter(this, convInfos);
        listView.setAdapter(adapter);

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


        SliderActivity sliderActivity = new SliderActivity();
        sliderActivity.attachSlider(this);

        setupOnTextInput();
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);

        processListUpdate();


    }


    public void back(View v) {
        onBackPressed();
    }


    public void pickFiles(View v) {
        final FilePickerBottomSheet filePickerBottomSheet = new FilePickerBottomSheet();
        filePickerBottomSheet.show(getSupportFragmentManager(), "blocked");
        filePickerBottomSheet.setRunnable(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               final ImageFile imageFile = new ImageFile(filePickerBottomSheet.getImages().get(position));
               Thread thread = new Thread(new Runnable() {
                   @Override
                   public void run() {
                       try {
                           final UploadImageResponse uploadImageResponse = (new UploadImageRequest(imageFile, CachedValues.getSessionKey(getApplicationContext()))).execute();
                           if(!uploadImageResponse.isSuccess())
                           {
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       CuteToast.showError("Failed upload image!", MessagingActivity.this);
                                   }

                               });
                           }
                           else
                           {
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       try {
                                           CuteToast.showSuccess("Success " + uploadImageResponse.getMediaId(), MessagingActivity.this);
                                       } catch (ResponseException e) {
                                           e.printStackTrace();
                                       }
                                   }

                               });
                               mediaIdToSend = uploadImageResponse.getMediaId();
                           }
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                   }
               });
               thread.start();

            }
        });
    }


    public void showInfo(View v) {
        final ConversationBottomSheet conversationBottomSheet = new ConversationBottomSheet();
        conversationBottomSheet.setCid(String.valueOf(cid));
        conversationBottomSheet.show(getSupportFragmentManager(), "blocked");
        conversationBottomSheet.setCancelable(true);
    }






    public boolean isrecording = false;




    @Override
    protected void onStop() {
        super.onStop();
        closed = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closed = true;
        ChatsListAdapter.canOpen = true;
    }


    public boolean isRecordPanelShown = false;
    private Thread recordWaiter;


    private void processListUpdate() {

        // Обработка ответа JSON
        Thread loadThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    GetMessageListResponse getMessageListResponse = (new GetMessageListRequest(CachedValues.getSessionKey(getApplicationContext()), String.valueOf(cid))).execute();
                    if (getMessageListResponse.isSuccess()) {
                        List<Message> messages = getMessageListResponse.getMessages();
                        for (final Message message : messages) {


                            final boolean my;

                            my = (message.getAccountId() == Integer.parseInt(CachedValues.getId(getApplicationContext())));

                            final int id = message.getId();
                            final int authorId = message.getAccountId();

                            boolean isInfo = false;
                            final String messageText;
                            if (message.getType().equals(Message.Type.SERVICE)) {
                                isInfo = true;
                                ServiceData serviceData = message.getServiceData();
                                if(serviceData.getType().equals(ServiceData.Types.CHAT_CREATED))
                                {
                                    messageText = StringsRepository.getOrDefault(R.string.chat_created, getApplicationContext())
                                            .replace("%s", serviceData.getChatName());
                                }
                                else
                                {
                                    messageText = message.getText();
                                }

                            }
                            else
                            {
                                messageText = message.getText();
                            }
                            isEmpty = false;
                            final boolean finalIsInfo = isInfo;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!ids.containsKey(String.valueOf(id))) {
                                        MessageInfo messageInfo = new MessageInfo(String.valueOf(id), message.getDisplayName(),
                                                messageText, my, false, Numbers.getTimeFromTimestamp(message.getTime(), getApplicationContext()), false,
                                                authorId, finalIsInfo, message.getAvatarPath(), message.getAttachmentPath(), message.getAttachmentData());
                                        ids.put(String.valueOf(id), messageInfo);

                                        adapter.add(messageInfo);
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


    public void sendMessage(View view) {

        EditText messageView = findViewById(R.id.message_box);

        final String message = String.valueOf(messageView.getText());
        messageView.setText("");
        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final SendMessageResponse sendMessageResponse = (new SendMessageRequest(CachedValues.getSessionKey(getApplicationContext()), String.valueOf(cid), message, mediaIdToSend)).execute();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MessageInfo messageInfo = null;
                            try {
                                messageInfo = new MessageInfo(String.valueOf(sendMessageResponse.getId()), name,
                                        sendMessageResponse.getText(), true, false, Numbers.getTimeFromTimestamp(sendMessageResponse.getTime(), getApplicationContext()), false,
                                        Integer.parseInt(CachedValues.getId(getApplicationContext())), false, null, sendMessageResponse.getAttachmentPath(), sendMessageResponse.getAttachmentData());
                                mediaIdToSend = "";
                                ids.put(String.valueOf((sendMessageResponse.getId())), messageInfo);

                                adapter.add(messageInfo);
                            } catch (ResponseException e) {
                                e.printStackTrace();
                            } catch (NotCachedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (NotCachedException e) {
                    e.printStackTrace();
                } catch (ResponseException e) {
                    e.printStackTrace();
                }
            }
        });
        sendThread.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        //  overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}