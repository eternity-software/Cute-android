package ru.etysoft.cute.activities.messaging;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.components.ForwardedMessage;
import ru.etysoft.cute.utils.Numbers;
import ru.etysoft.cute.utils.StringFormatter;
import ru.etysoft.cuteframework.exceptions.NotCachedException;
import ru.etysoft.cuteframework.models.Chat;
import ru.etysoft.cuteframework.models.messages.SuperMessage;
import ru.etysoft.cuteframework.storage.Cache;

public class MessagesAdapter extends RecyclerView.Adapter<MessageViewHolder> {


    private List<MessageComponent> itemList = new ArrayList<>();
    private List<String> ids = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private String chatType;

    public MessagesAdapter(Context context, String chatType) {
        layoutInflater = LayoutInflater.from(context);
        this.chatType = chatType;

    }


    @Override
    public int getItemViewType(int position) {


        MessageComponent messageComponent = itemList.get(position);
        try {
            if (messageComponent.getMessage() instanceof SuperMessage) {
                SuperMessage superMessage = (SuperMessage) messageComponent.getMessage();
                if (!chatType.equals(Chat.TYPE_PRIVATE)) {
                    if (position > 0) {

                        MessageComponent previousMessageComponent = itemList.get(position - 1);
                        if (previousMessageComponent.getMessage() instanceof SuperMessage) {


                            if (!superMessage.getSender().getId().equals(Cache.getUserAccount().getId())) {

                                SuperMessage previousSuperMessage = (SuperMessage) previousMessageComponent.getMessage();
                                if (superMessage.getSender().getId().equals(previousSuperMessage.getSender().getId())) {
                                    itemList.get(position).setType(MessageComponent.TYPE_CHAT_MESSAGE);
                                } else {
                                    itemList.get(position).setType(MessageComponent.TYPE_CHAT_ANNOTATION_MESSAGE);
                                }
                            }


                        } else if (!(previousMessageComponent.getMessage() instanceof SuperMessage)) {

                            if (!superMessage.getSender().getId().equals(Cache.getUserAccount().getId())) {
                                itemList.get(position).setType(MessageComponent.TYPE_CHAT_ANNOTATION_MESSAGE);
                            }
                        }
                    } else {
                        if (!superMessage.getSender().getId().equals(Cache.getUserAccount().getId())) {
                            itemList.get(position).setType(MessageComponent.TYPE_CHAT_ANNOTATION_MESSAGE);
                        }
                    }
                } else {
                    if (messageComponent.getMessage() instanceof SuperMessage) {

                        if (!superMessage.getSender().getId().equals(Cache.getUserAccount().getId())) {
                            itemList.get(position).setType(MessageComponent.TYPE_DIALOG_MESSAGE);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemList.get(position).getType();
    }

    public void insertRange(int index, List<MessageComponent> messageComponentList) {
        List<MessageComponent> finalMessages = new ArrayList<>();

        for(MessageComponent messageComponent : messageComponentList)
        {
            if(messageComponent.getMessage() != null)
            {
                if(ids.contains(messageComponent.getMessage().getId()))
                {

                }
                else
                {
                    finalMessages.add(messageComponent);
              //      ids.add(messageComponent.getMessage().getId());
                }
            }
        }
        itemList.addAll(index, finalMessages);
    }

    public MessageComponent getMessageComponent(int index) {

        return itemList.get(index);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageViewHolder viewHolder = new MessageViewHolder(layoutInflater.inflate(R.layout.info_message, parent, false));
        if (viewType == MessageComponent.TYPE_MY_MESSAGE) {
            viewHolder = new MessageViewHolder(layoutInflater.inflate(R.layout.conv_mymessage, parent, false));
        } else if (viewType == MessageComponent.TYPE_DIALOG_MESSAGE) {
            viewHolder = new MessageViewHolder(layoutInflater.inflate(R.layout.dialog_message, parent, false));
        } else if (viewType == MessageComponent.TYPE_CHAT_ANNOTATION_MESSAGE) {
            viewHolder = new MessageViewHolder(layoutInflater.inflate(R.layout.chat_message, parent, false));
        } else if (viewType == MessageComponent.TYPE_CHAT_MESSAGE) {
            viewHolder = new MessageViewHolder(layoutInflater.inflate(R.layout.chat_messagenoinfo, parent, false));
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageComponent messageComponent = itemList.get(position);

        if (messageComponent.getMessage() instanceof SuperMessage) {

            SuperMessage superMessage = (SuperMessage) messageComponent.getMessage();
            View view = holder.getMessageView();

            TextView messageBody = view.findViewById(R.id.messageBody);
            TextView emojiView = view.findViewById(R.id.emojiView);
            TextView timeView = view.findViewById(R.id.timeview);
            ImageView state = view.findViewById(R.id.state);
            LinearLayout messageContainer = view.findViewById(R.id.messageContainer);
            ForwardedMessage forwardedMessage = view.findViewById(R.id.forwardedMessage);

            String messageText = superMessage.getText();

            forwardedMessage.setVisibility(View.GONE);

            if (StringFormatter.isEmoji(messageText) && messageText.length() < 10) {
                messageContainer.setVisibility(View.GONE);
                emojiView.setVisibility(View.VISIBLE);
                emojiView.setText(superMessage.getText());
            } else {
                emojiView.setVisibility(View.GONE);
                messageContainer.setVisibility(View.VISIBLE);
                messageBody.setText(superMessage.getText());
            }

            String senderId = superMessage.getSender().getId();

            if(messageComponent.getType() == MessageComponent.TYPE_CHAT_ANNOTATION_MESSAGE)
            {
                TextView displayNameView = view.findViewById(R.id.displayNameView);
                displayNameView.setText(superMessage.getSender().getName());
                Avatar avatar = view.findViewById(R.id.avatarView);
                avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }


            if (messageComponent.getState() == MessageComponent.STATE_SENT) {
                String time = Numbers.getTimeFromTimestamp(Long.parseLong(superMessage.getCreatedTime() + "000"), layoutInflater.getContext());
                timeView.setText(time);
                if (messageComponent.getType() == MessageComponent.TYPE_MY_MESSAGE) {
                    state.setVisibility(View.GONE);
                }
            }


        }


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
