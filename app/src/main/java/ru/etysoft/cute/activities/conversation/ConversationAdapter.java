package ru.etysoft.cute.activities.conversation;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
        ConversationInfo info = list.get(position);

        if (convertView == null) {
            final LayoutInflater inflator = context.getLayoutInflater();

            if (!info.isConversation()) {
                if (info.isMine()) {
                    view = inflator.inflate(R.layout.conv_mymessage, null);
                } else {
                    view = inflator.inflate(R.layout.dialog_message, null);
                }

            }

            final ConversationAdapter.ViewHolder viewHolder = new ConversationAdapter.ViewHolder();

            viewHolder.time = (TextView) view.findViewById(R.id.timeview);
            viewHolder.message = (TextView) view.findViewById(R.id.message_body);

            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.time.setText(info.getSubtext());
        holder.message.setText(info.getLastmessage());
        return view;
    }

    static class ViewHolder {
        protected TextView time;
        protected TextView message;
    }
}
