package ru.etysoft.cute.activities.conversation;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.Profile;
import ru.etysoft.cute.utils.ImagesWorker;

public class ConversationAdapter extends ArrayAdapter<ConversationInfo> {
    private final Activity context;
    private final List<ConversationInfo> list;

    public ConversationAdapter(Activity context, List<ConversationInfo> values) {
        super(context, R.layout.dialog_element, values);
        this.context = context;
        this.list = values;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View view = null;

        // Получение экземпляра сообщения по позиции
        final ConversationInfo info = list.get(position);


        final LayoutInflater inflator = context.getLayoutInflater();
        boolean isFirstAid = false;

        // Проверка на беседу
        if (!info.isDialog()) {
            // Проверка на своё сообщение
            if (info.isMine() && !info.isInfo()) {
                view = inflator.inflate(R.layout.conv_mymessage, null);
            } else if (info.isInfo()) {
                view = inflator.inflate(R.layout.info_message, null);
            } else {
                if (position == 0) {
                    isFirstAid = true;
                    view = inflator.inflate(R.layout.chat_message, null);
                } else {
                    if (list.get(position - 1).getAid() != info.getAid()) {
                        isFirstAid = true;
                        view = inflator.inflate(R.layout.chat_message, null);
                    } else {
                        view = inflator.inflate(R.layout.chat_messagenoinfo, null);
                    }
                }
            }

        } else {

        }


        // Инициализируем элементы
        final ConversationAdapter.ViewHolder viewHolder = new ConversationAdapter.ViewHolder();

        if (!info.isInfo()) {
            // Не информационное сообщение

            viewHolder.time = (TextView) view.findViewById(R.id.timeview);
            viewHolder.message = (TextView) view.findViewById(R.id.message_body);
            viewHolder.back = view.findViewById(R.id.messageback);
            if (isFirstAid) {
                viewHolder.name = view.findViewById(R.id.nickname);
                viewHolder.userpic = view.findViewById(R.id.userpic);
            }

            view.setTag(viewHolder);

            // Задаём контент
            final ViewHolder holder = (ViewHolder) view.getTag();
            if (!info.isReaded()) {
                holder.back.setBackgroundColor(context.getResources().getColor(R.color.colorNotReaded));
            } else {
                holder.back.setBackgroundColor(context.getResources().getColor(R.color.colorBackground));
            }

            if (isFirstAid) {
                holder.name.setText(info.getName());
                ImagesWorker.setGradient(holder.userpic, info.getAid());
                holder.userpic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), Profile.class);
                        intent.putExtra("id", info.getAid());
                        getContext().startActivity(intent);
                    }
                });
            }

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    holder.message.setText("MESSAGE INFO: \n" +
                            "ID: " + info.getId() + "\n"
                            + "READED: " + info.isReaded() + "\n"
                            + "MY: " + info.isMine() + "\n"
                            + "NAME: " + info.getName() + "\n"
                            + "AID: " + info.getAid()
                    );
                }
            });

            holder.time.setText(info.getSubtext());
            holder.message.setText(info.getMessage());
        } else {

            viewHolder.message = (TextView) view.findViewById(R.id.infotext);
            view.setTag(viewHolder);
            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.message.setText(info.getMessage());
        }

        return view;
    }


    // Держим информацию
    static class ViewHolder {
        protected TextView time;
        protected TextView message;
        protected RelativeLayout back;
        protected TextView name;
        protected ImageView userpic;
    }
}
