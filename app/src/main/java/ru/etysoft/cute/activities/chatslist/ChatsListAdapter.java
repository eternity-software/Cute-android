package ru.etysoft.cute.activities.chatslist;

import android.app.Activity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.messaging.MessagingActivity;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.lang.CustomLanguage;
import ru.etysoft.cute.utils.Numbers;

import ru.etysoft.cuteframework.models.Chat;

import ru.etysoft.cuteframework.models.ChatSnippet;
import ru.etysoft.cuteframework.models.messages.ChatCreatedData;
import ru.etysoft.cuteframework.models.messages.Message;
import ru.etysoft.cuteframework.models.messages.ServiceData;
import ru.etysoft.cuteframework.models.messages.ServiceMessage;
import ru.etysoft.cuteframework.models.messages.SuperMessage;
import ru.etysoft.cuteframework.storage.Cache;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.ChatsViewHolder> {
    private final Activity context;
    private final List<ChatSnippet> list;
    public static boolean isMessagingActivityOpened = true;
    private LayoutInflater layoutInflater;

    public ChatsListAdapter(Activity context, List<ChatSnippet> values) {
        this.context = context;
        this.list = values;
        layoutInflater = LayoutInflater.from(context);
    }





    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ChatsViewHolder((layoutInflater.inflate(R.layout.dialog_element, parent, false)));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {
        View view = holder.mainView;

        try {
            // Инициализируем информацию о беседе или диалоге
            final ChatSnippet info = list.get(position);






            // Инициализируем подэлементы



            // Задаём обработчик нажатия
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isMessagingActivityOpened) {
                        isMessagingActivityOpened = false;
                        if (info.getType().equals(Chat.TYPE_PRIVATE)) {

                            MessagingActivity.openActivityForDialog(context, info.getId());
                        } else {
                            MessagingActivity.openActivityForChat(context, info.getId(), info.getName());

                        }

                    }
                }
            });




            boolean isDialog = (info.getType().equals(Chat.TYPE_PRIVATE));


            if (isDialog) {

                if (true) {
                    holder.avatarView.setOnline(true);
                } else {
                    holder.avatarView.setOnline(false);
                }

            }

            //TODO: Update read state
            boolean isLastMessageRead = false;
            if (isLastMessageRead) {
                TypedValue selectableBackground = new TypedValue();
                context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, selectableBackground, true);
                //   holder.container.setBackground(context.getResources().getDrawable(selectableBackground.resourceId));
                holder.readstatusView.setVisibility(View.INVISIBLE);
            } else {
                holder.readstatusView.setVisibility(View.VISIBLE);
            }


            if (holder.avatarView != null) {
                // holder.avatar.showAnimate();
                if (info.getType().equals(Chat.TYPE_PRIVATE)) {

                    holder.avatarView.generateIdPicture(1);
                } else {
                    holder.avatarView.generateIdPicture(2);
                }

                holder.avatarView.setAcronym(info.getName(), Avatar.Size.MEDIUM);

                // TODO: set avatar
                if (false) {
                    // Picasso.get().load(info.getAvatarPath()).transform(new CircleTransform()).into(holder.avatar.getPictureView());
                }
            }


            holder.nameView.setText(info.getName());


            Message lastMessage = info.getLastMessage();


            if (!(lastMessage instanceof ServiceMessage)) {

                if (lastMessage instanceof SuperMessage) {

                    SuperMessage superMessage = (SuperMessage) lastMessage;

                    if (!superMessage.getSender().getId().equals(Cache.getUserAccount().getId())){
                        if (!isDialog) {
                            holder.accentView.setText(superMessage.getSender().getName() + ": ");
                        } else {
                            holder.accentView.setText("");
                        }

                    } else{
                        holder.accentView.setText(CustomLanguage.getStringsRepository().getOrDefault(R.string.you, context) + ": ");
                    }

                    holder.messageView.setText(superMessage.getText());
                }

            } else {


                ServiceData serviceData = ((ServiceMessage) lastMessage).getServiceData();
                String messageText;
                if (serviceData instanceof ChatCreatedData) {

                    messageText = CustomLanguage.getStringsRepository().getOrDefault(R.string.chat_created, context)
                            .replace("%s", ((ChatCreatedData) serviceData).getChatName());
                } else {

                    messageText = CustomLanguage.getStringsRepository().getOrDefault(R.string.unknown_service_message, context);

                }
                holder.accentView.setText(messageText);
                holder.messageView.setText("");

            }

            // TODO: add time handler
            holder.timeView.setText(Numbers.getTimeFromTimestamp(0, context));

            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setFillAfter(false);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(800);

            // TODO: add attachments check
//            if(info.getMessage().getAttachmentData() != null)
//            {
//                holder.messageView.setText("");
//                holder.accentView.setText(holder.accentView.getText() + CustomLanguage.getStringsRepository().getOrDefault(R.string.image, getContext()));
//            }

            //holder.container.startAnimation(fadeIn);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<ChatSnippet> getList() {
        return list;
    }

    // Держим данные
    public static class ChatsViewHolder extends RecyclerView.ViewHolder {
         TextView nameView;
         TextView accentView;
         TextView messageView;
         Avatar avatarView;
         TextView timeView;
         TextView readstatusView;
         LinearLayout container;
         ImageView onlineView;
         View mainView;

        public ChatsViewHolder(@NonNull View view) {
            super(view);
            mainView = view;
            nameView = view.findViewById(R.id.label);
            messageView = view.findViewById(R.id.message);
            timeView = view.findViewById(R.id.time);
            accentView = view.findViewById(R.id.message_accent);
            readstatusView = view.findViewById(R.id.readed);
            avatarView = view.findViewById(R.id.avatar_component);
            container = view.findViewById(R.id.container);
        }

        public TextView getNameView() {
            return nameView;
        }

        public TextView getAccentView() {
            return accentView;
        }

        public TextView getMessageView() {
            return messageView;
        }

        public Avatar getAvatarView() {
            return avatarView;
        }

        public TextView getTimeView() {
            return timeView;
        }

        public TextView getReadstatusView() {
            return readstatusView;
        }

        public LinearLayout getContainer() {
            return container;
        }

        public ImageView getOnlineView() {
            return onlineView;
        }
    }
}
