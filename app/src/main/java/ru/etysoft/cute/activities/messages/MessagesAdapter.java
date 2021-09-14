package ru.etysoft.cute.activities.messages;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.ImagePreview;
import ru.etysoft.cute.activities.MessagingActivity;
import ru.etysoft.cute.activities.Profile;
import ru.etysoft.cute.components.Attachments;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.components.MessageTextView;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.lang.StringsRepository;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cute.utils.Numbers;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.chat.ServiceData;
import ru.etysoft.cuteframework.methods.messages.AttachmentData;
import ru.etysoft.cuteframework.methods.messages.Message;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final MessagingActivity context;
    private final List<Message> list;
    private final LayoutInflater inflater;
    private boolean isDialog;
    private RecyclerView recyclerView;

    private long firstmessageId;

    public MessagesAdapter(MessagingActivity context, List<Message> values, boolean isDialog, RecyclerView recyclerView) {
        this.context = context;
        this.list = values;
        this.isDialog = isDialog;
        this.inflater = LayoutInflater.from(context);
        this.firstmessageId = firstmessageId;
        this.recyclerView = recyclerView;

    }

    public void setFirstMessageId(long firstmessageId) {
        this.firstmessageId = firstmessageId;
    }

    private Set<Integer> animated = new HashSet<>();

    private void setAnimation(View viewToAnimate, final int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position == getItemCount() - 1 && !animated.contains(position)) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_slide_from_bottom);
            viewToAnimate.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animated.add(position);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case Types.SERVICE:
                viewHolder = new ViewHolder.Service(inflater.inflate(R.layout.info_message, parent, false));
                break;
            case Types.CONV:
                viewHolder = new ViewHolder.ChatMessage(inflater.inflate(R.layout.chat_messagenoinfo, parent, false));
                break;
            case Types.CONV_PREVIEW:
                viewHolder = new ViewHolder.ChatExpandedMessage(inflater.inflate(R.layout.chat_message, parent, false));
                break;
            case Types.DIALOG:
                viewHolder = new ViewHolder.DialogMessage(inflater.inflate(R.layout.dialog_message, parent, false));
                break;
            case Types.MINE:
                viewHolder = new ViewHolder.MyMessage(inflater.inflate(R.layout.conv_mymessage, parent, false));
                break;
            case Types.CLOUD:
                viewHolder = new ViewHolder.Clouds(inflater.inflate(R.layout.chat_beggining, parent, false));
                break;
        }

        return viewHolder;

    }

    private static boolean isEmoji(String message) {
        return message.matches("(?:[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83E\uDD00-\uD83E\uDDFF]|" +
                "[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|" +
                "[\u2600-\u26FF]\uFE0F?|[\u2700-\u27BF]\uFE0F?|\u24C2\uFE0F?|" +
                "[\uD83C\uDDE6-\uD83C\uDDFF]{1,2}|" +
                "[\uD83C\uDD70\uD83C\uDD71\uD83C\uDD7E\uD83C\uDD7F\uD83C\uDD8E\uD83C\uDD91-\uD83C\uDD9A]\uFE0F?|" +
                "[\u0023\u002A\u0030-\u0039]\uFE0F?\u20E3|[\u2194-\u2199\u21A9-\u21AA]\uFE0F?|[\u2B05-\u2B07\u2B1B\u2B1C\u2B50\u2B55]\uFE0F?|" +
                "[\u2934\u2935]\uFE0F?|[\u3030\u303D]\uFE0F?|[\u3297\u3299]\uFE0F?|" +
                "[\uD83C\uDE01\uD83C\uDE02\uD83C\uDE1A\uD83C\uDE2F\uD83C\uDE32-\uD83C\uDE3A\uD83C\uDE50\uD83C\uDE51]\uFE0F?|" +
                "[\u203C\u2049]\uFE0F?|[\u25AA\u25AB\u25B6\u25C0\u25FB-\u25FE]\uFE0F?|" +
                "[\u00A9\u00AE]\uFE0F?|[\u2122\u2139]\uFE0F?|\uD83C\uDC04\uFE0F?|\uD83C\uDCCF\uFE0F?|" +
                "[\u231A\u231B\u2328\u23CF\u23E9-\u23F3\u23F8-\u23FA]\uFE0F?)+");
    }

    private void setBasicInfo(final Message message, final ViewHolder.BasicMessageHolder basicMessageHolder, int type) {
        basicMessageHolder.text.setVisibility(View.GONE);
        basicMessageHolder.emoji.setVisibility(View.GONE);

        if (message.isRead()) {
            basicMessageHolder.rootView.setBackgroundColor(context.getResources().getColor(R.color.colorTransparent));
        } else {
            basicMessageHolder.rootView.setBackgroundColor(context.getResources().getColor(R.color.colorUnreadBackground));
        }

        final AttachmentData attachmentData = message.getAttachmentData();
        if (message.getText() != null && isEmoji(message.getText()) && message.getText().length() < 8 && attachmentData == null) {
            basicMessageHolder.emoji.setText(message.getText());
            basicMessageHolder.emoji.setVisibility(View.VISIBLE);
            basicMessageHolder.messageContainer.setBackground(null);
        } else {
            if (message.getText() != null) {
                if (message.getText().length() > 0) {
                    basicMessageHolder.text.setText(message.getText());
                    basicMessageHolder.text.setVisibility(View.VISIBLE);
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, Numbers.dpToPx(-8, context), 0, 0);
                basicMessageHolder.attachments.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 0);
                basicMessageHolder.attachments.setLayoutParams(params);
            }


            if (Types.MINE == type) {
                basicMessageHolder.messageContainer.setBackground(context.getResources().getDrawable(R.drawable.mymessage));
            } else {
                basicMessageHolder.messageContainer.setBackground(context.getResources().getDrawable(R.drawable.dialog_message));
            }
        }


        if (attachmentData != null) {
            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bmp = Bitmap.createBitmap(attachmentData.getWidth(), attachmentData.getHeight(), conf); // this creates a MUTABLE bitmap

            bmp.eraseColor(context.getResources().getColor(R.color.colorPlaceholder));
            basicMessageHolder.attachments.getImageView().setImageBitmap(bmp);

            Drawable drawable = new BitmapDrawable(context.getResources(), bmp);
            Picasso.get().load(message.getAttachmentPath()).placeholder(drawable).into(basicMessageHolder.attachments.getImageView());
            basicMessageHolder.attachments.getImageView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = null;
                    Intent intent = new Intent(context, ImagePreview.class);
                    intent.putExtra("url", message.getAttachmentPath());
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        final View imageView = basicMessageHolder.attachments.findViewById(R.id.imageContainer);
                        String transitionName = context.getString(R.string.transition_image_preview);
                        imageView.setTransitionName(transitionName);

                        // This part is important. We first need to clip this view to only its visible part.
                        // We will also clip the corresponding view in the SecondActivity using shared element
                        // callbacks.
                        Rect localVisibleRect = new Rect();
                        imageView.getLocalVisibleRect(localVisibleRect);
                        imageView.setClipBounds(localVisibleRect);



                        intent.putExtra(context.getResources().getString(R.string.transition_image_preview), transitionName);
                        intent.putExtra(ImagePreview.EXTRA_CLIP_RECT, localVisibleRect);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                                context,
                                Pair.create(imageView, transitionName));

                        context.setOnResume(new Runnable() {
                            @Override
                            public void run() {
                                if (imageView != null) {
                                    imageView.setClipBounds(null);
                                }
                                context.setOnResume(null);
                            }
                        });

                        context.startActivity(intent, options.toBundle());

                    } else {

                        context.startActivity(intent);

                    }


                }
            });
            basicMessageHolder.attachments.setVisibility(View.VISIBLE);
        } else {
            basicMessageHolder.attachments.setVisibility(View.GONE);
        }
        try {

            basicMessageHolder.time.setText(String.valueOf(Numbers.getTimeFromTimestamp(message.getTime() + "000", context)));

        } catch (Exception ignored) {

        }

    }

    public void updateMessageStatus(Message message, ViewHolder.MyMessage myMessage) {
        myMessage.state.setVisibility(View.VISIBLE);
        if (message.getId() == -2) {
            myMessage.state.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_error));
            myMessage.basicMessageHolder.time.setText("");
        } else if (message.getId() == -1) {
            myMessage.state.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_clock));
            myMessage.basicMessageHolder.time.setText("");
        } else if (message.getId() >= 0) {
            myMessage.state.setVisibility(View.GONE);
            setBasicInfo(message, myMessage.basicMessageHolder, Types.MINE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Message message = list.get(position);

        if (message != null) {


            switch (getItemViewType(position)) {
                case Types.SERVICE:
                    ViewHolder.Service serviceMessage = (ViewHolder.Service) holder;
                    String messageText = "";
                    try {
                        ServiceData serviceData = message.getServiceData();
                        if (serviceData.getType().equals(ServiceData.Types.CHAT_CREATED)) {

                            messageText = StringsRepository.getOrDefault(R.string.chat_created, inflater.getContext())
                                    .replace("%s", serviceData.getChatName());

                        } else if (serviceData.getType().equals(ServiceData.Types.ADD_MEMBER)) {
                            messageText = StringsRepository.getOrDefault(R.string.add_member, inflater.getContext())
                                    .replace("%s", serviceData.getDisplayName());
                        } else {
                            messageText = message.getText();
                        }
                    } catch (ResponseException e) {
                        e.printStackTrace();
                    }
                    serviceMessage.text.setText(messageText);
                    break;
                case Types.CONV_PREVIEW:
                    ViewHolder.ChatExpandedMessage chatExpandedMessage = (ViewHolder.ChatExpandedMessage) holder;
                    setBasicInfo(message, chatExpandedMessage.basicMessageHolder, Types.CONV_PREVIEW);
                    chatExpandedMessage.avatar.setAcronym(message.getSender().getDisplayName(), Avatar.Size.SMALL);
                    chatExpandedMessage.displayName.setText(message.getSender().getDisplayName());
                    chatExpandedMessage.avatar.generateIdPicture((int) message.getSender().getId());
                    if (message.getSender().getAvatar() != null) {
                        Picasso.get().load(message.getSender().getAvatar()).transform(new CircleTransform()).into(((ViewHolder.ChatExpandedMessage) holder).avatar.getPictureView());
                    }
                    ((ViewHolder.ChatExpandedMessage) holder).avatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, Profile.class);
                            intent.putExtra("id", message.getSender().getId());
                            context.startActivity(intent);
                        }
                    });
                    break;
                case Types.DIALOG:
                    ViewHolder.DialogMessage dialogMessage = (ViewHolder.DialogMessage) holder;
                    setBasicInfo(message, dialogMessage.basicMessageHolder, Types.DIALOG);
                    break;
                case Types.CONV:
                    ViewHolder.ChatMessage chatMessage = (ViewHolder.ChatMessage) holder;
                    setBasicInfo(message, chatMessage.basicMessageHolder, Types.CONV);
                    break;
                case Types.MINE:
                    final ViewHolder.MyMessage myMessage = (ViewHolder.MyMessage) holder;
                    setBasicInfo(message, myMessage.basicMessageHolder, Types.MINE);
                    message.setMessageDataHandler(new Message.MessageDataHandler() {
                        @Override
                        public void onDataUpdated(Message message) {
                            updateMessageStatus(message, myMessage);
                        }
                    });
                    myMessage.basicMessageHolder.rootView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myMessage.basicMessageHolder.text.setText("id " + message.getId());
                            updateMessageStatus(message, myMessage);
                        }
                    });
                    updateMessageStatus(message, myMessage);
                    break;

            }

            setAnimation(holder.itemView, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = list.get(position);
        try {
            if (message == null) {
                tempClouds = true;
                return Types.CLOUD;
            }
            if (message.getId() < 0) {
                return Types.MINE;
            } else if (message.getType().equals(Message.Type.SERVICE)) {
                return Types.SERVICE;
            } else if (message.getSender().getId() == Integer.parseInt(CachedValues.getId(context))) {
                return Types.MINE;
            } else if (!isDialog) {
                if (list.get(position - 1).getSender().getId() != message.getSender().getId() | list.get(position - 1).getType().equals(Message.Type.SERVICE)) {
                    return Types.CONV_PREVIEW;
                } else {
                    return Types.CONV;
                }
            } else {
                return Types.DIALOG;
            }
        } catch (Exception e) {
            System.out.println("Error processing " + position);
            e.printStackTrace();
            return Types.MINE;
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    boolean tempClouds = false;

    public void addItem(Message message) {
        if (!tempClouds && message.getId() - firstmessageId == 0) {
            tempClouds = true;
            addItem(null);
        }
        if (message == null) {
            list.add(null);
            notifyItemInserted(getItemCount() - 1);
        } else {
            boolean scrollToBottom = false;
            final int offset = recyclerView.computeVerticalScrollOffset();
            final int range = recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent();
            System.out.println("offset " + offset + ", range " + range);
            if (range - offset < (recyclerView.getHeight())) {
                scrollToBottom = true;
            }
            list.add(message);
            if (scrollToBottom) {
                recyclerView.smoothScrollToPosition(getItemCount() - 1);
            }

            notifyItemInserted(getItemCount() - 1);
        }
    }


    // Держим вьюшки
    static class ViewHolder {
        static class Service extends RecyclerView.ViewHolder {
            final TextView text;

            public Service(@NonNull View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.infotext);
            }
        }

        static class ChatExpandedMessage extends RecyclerView.ViewHolder {

            final TextView displayName;
            final Avatar avatar;
            final BasicMessageHolder basicMessageHolder;


            public ChatExpandedMessage(@NonNull View itemView) {
                super(itemView);
                displayName = itemView.findViewById(R.id.displayNameView);
                avatar = itemView.findViewById(R.id.avatarView);
                basicMessageHolder = new BasicMessageHolder(itemView);
            }
        }

        static class Clouds extends RecyclerView.ViewHolder {

            public Clouds(@NonNull View itemView) {
                super(itemView);
            }
        }

        static class ChatMessage extends RecyclerView.ViewHolder {
            final BasicMessageHolder basicMessageHolder;

            public ChatMessage(@NonNull View itemView) {
                super(itemView);
                basicMessageHolder = new BasicMessageHolder(itemView);
            }
        }

        static class DialogMessage extends RecyclerView.ViewHolder {
            final BasicMessageHolder basicMessageHolder;

            public DialogMessage(@NonNull View itemView) {
                super(itemView);
                basicMessageHolder = new BasicMessageHolder(itemView);
            }
        }

        static class MyMessage extends RecyclerView.ViewHolder {
            final BasicMessageHolder basicMessageHolder;
            final ImageView state;


            public MyMessage(@NonNull View itemView) {
                super(itemView);
                state = itemView.findViewById(R.id.state);
                basicMessageHolder = new BasicMessageHolder(itemView);
                basicMessageHolder.messageContainer.setBackground(itemView.getResources().getDrawable(R.drawable.mymessage));
            }
        }

        static class BasicMessageHolder {
            final TextView text;
            final TextView time;
            final TextView emoji;
            final Attachments attachments;
            final View rootView;
            final View messageContainer;

            public BasicMessageHolder(@NonNull View itemView) {
                text = itemView.findViewById(R.id.message_body);

                time = itemView.findViewById(R.id.timeview);
                attachments = itemView.findViewById(R.id.attachments);
                emoji = itemView.findViewById(R.id.emojiView);
                messageContainer = itemView.findViewById(R.id.messageContainer);
                rootView = itemView;

            }
        }


    }

    static class Types {
        public static final int SERVICE = 0;
        public static final int MINE = 1;
        public static final int CONV_PREVIEW = 2;
        public static final int CONV = 3;
        public static final int DIALOG = 4;
        public static final int CLOUD = 5;
    }
}
