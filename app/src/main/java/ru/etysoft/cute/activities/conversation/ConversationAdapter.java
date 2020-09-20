package ru.etysoft.cute.activities.conversation;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import ru.etysoft.cute.R;

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

        // Проверка на беседу
        if (!info.isConversation()) {

            // Проверка на своё сообщение
            if (info.isMine() && !info.isInfo()) {
                view = inflator.inflate(R.layout.conv_mymessage, null);
            } else if (info.isInfo()) {
                view = inflator.inflate(R.layout.info_message, null);
            } else {
                view = inflator.inflate(R.layout.dialog_message, null);
            }

        }


        // Инициализируем элементы
        final ConversationAdapter.ViewHolder viewHolder = new ConversationAdapter.ViewHolder();

        if (!info.isInfo()) {
            // Не информационное сообщение

            viewHolder.time = (TextView) view.findViewById(R.id.timeview);
            viewHolder.message = (TextView) view.findViewById(R.id.message_body);
            viewHolder.back = view.findViewById(R.id.messageback);

            view.setTag(viewHolder);

            // Задаём контент
            final ViewHolder holder = (ViewHolder) view.getTag();
            if (!info.isReaded()) {
                holder.back.setBackgroundColor(context.getResources().getColor(R.color.colorNotReaded));
            } else {
                holder.back.setBackgroundColor(context.getResources().getColor(R.color.colorBackground));
            }

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    holder.message.setText("MESSAGE INFO: \n" +
                            "ID: " + info.getId() + "\n"
                            + "READED: " + info.isReaded() + "\n"
                            + "MY: " + info.isMine() + "\n"
                            + "AID: " + info.getAid()
                    );
                }
            });

            holder.time.setText(info.getSubtext());
            holder.message.setText(info.getMessage());
        } else {
            final ViewHolder holder = (ViewHolder) view.getTag();
            viewHolder.message = (TextView) view.findViewById(R.id.message_body);
            holder.message.setText(info.getMessage());
        }

        return view;
    }


    // Держим информацию
    static class ViewHolder {
        protected TextView time;
        protected TextView message;
        protected RelativeLayout back;
    }
}