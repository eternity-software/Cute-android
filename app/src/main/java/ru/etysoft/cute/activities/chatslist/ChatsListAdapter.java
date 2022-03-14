package ru.etysoft.cute.activities.chatslist;

import android.app.Activity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.messaging.MessagingActivity;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.lang.CustomLanguage;
import ru.etysoft.cute.utils.Numbers;
import ru.etysoft.cuteframework.exceptions.ResponseException;

import ru.etysoft.cuteframework.models.Chat;

import ru.etysoft.cuteframework.models.ChatSnippet;
import ru.etysoft.cuteframework.models.messages.ChatCreatedData;
import ru.etysoft.cuteframework.models.messages.Message;
import ru.etysoft.cuteframework.models.messages.ServiceData;
import ru.etysoft.cuteframework.models.messages.ServiceMessage;
import ru.etysoft.cuteframework.models.messages.SuperMessage;
import ru.etysoft.cuteframework.storage.Cache;

public class ChatsListAdapter extends ArrayAdapter<ChatSnippet> {
    private final Activity context;
    private final List<ChatSnippet> list;
    public static boolean isMessagingActivityOpened = true;

    public ChatsListAdapter(Activity context, List<ChatSnippet> values) {
        super(context, R.layout.dialog_element, values);
        this.context = context;
        this.list = values;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View view = null;

        try {
            // Инициализируем информацию о беседе или диалоге
            final ChatSnippet info = list.get(position);

            final LayoutInflater inflator = context.getLayoutInflater();

            view = inflator.inflate(R.layout.dialog_element, null);


            final ViewHolder viewHolder = new ViewHolder();

            // Инициализируем подэлементы
            viewHolder.name = view.findViewById(R.id.label);
            viewHolder.messageView = view.findViewById(R.id.message);
            viewHolder.time = view.findViewById(R.id.time);
            viewHolder.accentView = view.findViewById(R.id.message_accent);
            viewHolder.readstatus = view.findViewById(R.id.readed);

            viewHolder.avatar = view.findViewById(R.id.avatar_component);
            viewHolder.container = view.findViewById(R.id.container);


            // Задаём обработчик нажатия
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isMessagingActivityOpened) {
                        isMessagingActivityOpened = false;
                        if (info.getType().equals(Chat.TYPE_PRIVATE)) {

                            MessagingActivity.openActivityForDialog(getContext(), info.getId());
                        } else {
                            MessagingActivity.openActivityForChat(getContext(), info.getId(), info.getName());

                        }

                    }
                }
            });

            view.setTag(viewHolder);

            ViewHolder holder = (ViewHolder) view.getTag();

            boolean isDialog = (info.getType().equals(Chat.TYPE_PRIVATE));


            if (isDialog) {

                if (true) {
                    holder.avatar.setOnline(true);
                } else {
                    holder.avatar.setOnline(false);
                }

            }

            //TODO: Update read state
            boolean isLastMessageRead = false;
            if (isLastMessageRead) {
                TypedValue selectableBackground = new TypedValue();
                getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, selectableBackground, true);
                //   holder.container.setBackground(context.getResources().getDrawable(selectableBackground.resourceId));
                holder.readstatus.setVisibility(View.INVISIBLE);
            } else {
                holder.readstatus.setVisibility(View.VISIBLE);
            }


            if (holder.avatar != null) {
                holder.avatar.showAnimate();
                if (info.getType().equals(Chat.TYPE_PRIVATE)) {

                    holder.avatar.generateIdPicture(1);
                } else {
                    holder.avatar.generateIdPicture(2);
                }

                holder.avatar.setAcronym(info.getName(), Avatar.Size.MEDIUM);

                // TODO: set avatar
                if (false) {
                    // Picasso.get().load(info.getAvatarPath()).transform(new CircleTransform()).into(holder.avatar.getPictureView());
                }
            }


            holder.name.setText(info.getName());


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

                    messageText = CustomLanguage.getStringsRepository().getOrDefault(R.string.chat_created, getContext())
                            .replace("%s", ((ChatCreatedData) serviceData).getChatName());
                } else {

                    messageText = CustomLanguage.getStringsRepository().getOrDefault(R.string.unknown_service_message, getContext());

                }
                holder.accentView.setText(messageText);
                holder.messageView.setText("");

            }

            // TODO: add time handler
            holder.time.setText(Numbers.getTimeFromTimestamp(0, context));

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

            holder.container.startAnimation(fadeIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }


    // Держим данные
    static class ViewHolder {
        protected TextView name;
        protected TextView accentView;
        protected TextView messageView;
        protected Avatar avatar;
        protected TextView time;
        protected TextView readstatus;
        protected LinearLayout container;
        protected ImageView online;
    }
}
