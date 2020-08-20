package ru.etysoft.cute.activities.dialogs;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.etysoft.cute.R;

public class DialogAdapter extends ArrayAdapter<DialogInfo> {
    private final Activity context;
    private final List<DialogInfo> list;

    public DialogAdapter(Activity context, List<DialogInfo> values) {
        super(context, R.layout.dialog_element, values);
        this.context = context;
        this.list = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.dialog_element, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.label);
            viewHolder.message = (TextView) view.findViewById(R.id.message);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(list.get(position).getName());
        holder.message.setText(list.get(position).getLastmessage());
        return view;
    }

    static class ViewHolder {
        protected TextView name;
        protected TextView message;
    }
}
