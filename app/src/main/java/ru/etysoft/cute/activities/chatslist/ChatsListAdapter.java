package ru.etysoft.cute.activities.chatslist;

import android.app.Activity;
import android.content.Intent;
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

import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.MessagingActivity;
import ru.etysoft.cute.components.Avatar;

public class ChatsListAdapter extends ArrayAdapter<ChatSnippetInfo> {
    private final Activity context;
    private final List<ChatSnippetInfo> list;
    public static boolean canOpen = true;

    public ChatsListAdapter(Activity context, List<ChatSnippetInfo> values) {
        super(context, R.layout.dialog_element, values);
        this.context = context;
        this.list = values;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View view = null;

        // Инициализируем информацию о беседе или диалоге
        final ChatSnippetInfo info = list.get(position);

        final LayoutInflater inflator = context.getLayoutInflater();

        view = inflator.inflate(R.layout.dialog_element, null);


        final ViewHolder viewHolder = new ViewHolder();

        // Инициализируем подэлементы
        viewHolder.name = (TextView) view.findViewById(R.id.label);
        viewHolder.messageView = (TextView) view.findViewById(R.id.message);
        viewHolder.time = (TextView) view.findViewById(R.id.time);
        viewHolder.accentView = (TextView) view.findViewById(R.id.message_accent);
        viewHolder.readstatus = (TextView) view.findViewById(R.id.readed);
        viewHolder.online = view.findViewById(R.id.status);
        viewHolder.avatar = (Avatar) view.findViewById(R.id.avatar_component);
        viewHolder.container = view.findViewById(R.id.container);


        // Задаём обработчик нажатия
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canOpen) {
                    canOpen = false;
                    Intent intent = new Intent(getContext(), MessagingActivity.class);
                    intent.putExtra("cid", info.getCid());
                    intent.putExtra("isd", info.isDialog());
                    intent.putExtra("name", info.getName());
                    intent.putExtra("cover", info.getCover());
                    getContext().startActivity(intent);
                }
            }
        });
        view.setTag(viewHolder);

        // Задаём персональные значения
        ViewHolder holder = (ViewHolder) view.getTag();


        if (info.isDialog()) {
            if (info.isOnline()) {
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
        if (info.getCountRead() != 0) {
            holder.readstatus.setText(String.valueOf(info.getCountRead()));
        }

        if (holder.avatar != null) {
            holder.avatar.generateIdPicture(Integer.parseInt(info.getCid()));
            holder.avatar.setAcronym(info.getName());
        }

        if (!info.getCover().equals("null")) {

        }

        holder.name.setText(info.getName());

        holder.accentView.setText(info.getSenderName() + ": ");
        holder.messageView.setText(info.getLastMessage());

        holder.time.setText(info.getTime());

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
