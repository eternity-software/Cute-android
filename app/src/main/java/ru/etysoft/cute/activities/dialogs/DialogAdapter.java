package ru.etysoft.cute.activities.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.Conversation;

public class DialogAdapter extends ArrayAdapter<DialogInfo> {
    private final Activity context;
    private final List<DialogInfo> list;

    public DialogAdapter(Activity context, List<DialogInfo> values) {
        super(context, R.layout.dialog_element, values);
        this.context = context;
        this.list = values;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            final LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.dialog_element, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.label);
            viewHolder.message = (TextView) view.findViewById(R.id.message);
            viewHolder.acronym = (TextView) view.findViewById(R.id.acronym);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), Conversation.class);
                    intent.putExtra("cid", list.get(position).getCid());
                    getContext().startActivity(intent);
                }
            });
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(list.get(position).getName());
        holder.message.setText(list.get(position).getLastmessage());
        holder.acronym.setText(list.get(position).getAcronym());
        return view;
    }

    static class ViewHolder {
        protected TextView name;
        protected TextView message;
        protected TextView acronym;
    }
}
