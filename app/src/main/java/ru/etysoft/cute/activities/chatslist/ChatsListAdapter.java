package ru.etysoft.cute.activities.chatslist;

import android.app.Activity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import ru.etysoft.cute.activities.MessagingActivity;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.lang.StringsRepository;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cute.utils.Numbers;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.chat.ChatSnippet;
import ru.etysoft.cuteframework.methods.chat.ServiceData;
import ru.etysoft.cuteframework.methods.messages.Message;

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
        viewHolder.online = view.findViewById(R.id.statusView);
        viewHolder.avatar = view.findViewById(R.id.avatar_component);
        viewHolder.container = view.findViewById(R.id.container);


        // Задаём обработчик нажатия
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMessagingActivityOpened) {
                    isMessagingActivityOpened = false;
                    MessagingActivity.openActivity(getContext(), info.getId(),
                            (info.getType().equals(ChatSnippet.Types.PRIVATE)),
                            info.getName(),
                            info.getAvatarPath());
                }
            }
        });

        view.setTag(viewHolder);

        ViewHolder holder = (ViewHolder) view.getTag();

        boolean isDialog = (info.getType().equals(ChatSnippet.Types.PRIVATE));

        if (isDialog) {

            if (true) {
                holder.online.setBackground(context.getResources().getDrawable(R.drawable.circle_online));
            } else {
                holder.online.setBackground(context.getResources().getDrawable(R.drawable.circle_offline));
            }

        } else {
            holder.online.setVisibility(View.INVISIBLE);
        }

        if (info.isRead()) {
            TypedValue selectableBackground = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, selectableBackground, true);
            holder.container.setBackgroundResource(selectableBackground.resourceId);
            holder.readstatus.setVisibility(View.INVISIBLE);
        } else {
            holder.readstatus.setVisibility(View.VISIBLE);
        }


        if (holder.avatar != null) {
            holder.avatar.showAnimate();
            holder.avatar.generateIdPicture(info.getId());
            holder.avatar.setAcronym(info.getName(), Avatar.Size.MEDIUM);
            if(info.getAvatarPath() != null)
            {
                Picasso.get().load(info.getAvatarPath()).transform(new CircleTransform()).into(holder.avatar.getPictureView());
            }
        }



        holder.name.setText(info.getName());

        if (!info.getMessageType().equals(Message.Type.SERVICE))
        {
            try {
                if(info.getLastMessageSenderId() != Integer.parseInt(CachedValues.getId(context)))
                {
                    holder.accentView.setText(info.getLastMessageSenderDisplayName() + ": ");
                }
                else
                {
                    holder.accentView.setText(StringsRepository.getOrDefault(R.string.your_message, context) + ": ");
                }
            } catch (NotCachedException e) {
                holder.accentView.setText(info.getLastMessageSenderDisplayName() + ": ");
                e.printStackTrace();
            }
            holder.messageView.setText(info.getLastMessageText());
        }
        else
        {
            ServiceData serviceData = info.getServiceData();
            if(serviceData.getType().equals(ServiceData.Types.CHAT_CREATED))
            {
                try {
                    String messageText = StringsRepository.getOrDefault(R.string.chat_created, getContext())
                            .replace("%s", serviceData.getChatName());
                    holder.accentView.setText(messageText);
                    holder.messageView.setText("");
                } catch (ResponseException e) {
                    e.printStackTrace();
                }
            }

        }
        holder.time.setText(Numbers.getTimeFromTimestamp(info.getLastMessageTime(), context));

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setFillAfter(false);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(800);

        holder.container.startAnimation(fadeIn);

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
