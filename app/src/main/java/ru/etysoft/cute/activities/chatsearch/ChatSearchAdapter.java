package ru.etysoft.cute.activities.chatsearch;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.etysoft.cute.AlertDialog;
import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.Conversation;
import ru.etysoft.cute.utils.ImagesWorker;

public class ChatSearchAdapter extends ArrayAdapter<ChatSearchInfo> {
    private final Activity context;
    private final List<ChatSearchInfo> list;

    public ChatSearchAdapter(Activity context, List<ChatSearchInfo> values) {
        super(context, R.layout.dialog_element, values);
        this.context = context;
        this.list = values;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View view = null;
        final ChatSearchInfo info = list.get(position);
        final LayoutInflater inflator = context.getLayoutInflater();

        view = inflator.inflate(R.layout.searchchat_element, null);

        final ViewHolder viewHolder = new ViewHolder();

        viewHolder.name = (TextView) view.findViewById(R.id.label);
        viewHolder.acronym = (TextView) view.findViewById(R.id.acronym);
        viewHolder.photo = view.findViewById(R.id.icon);
        viewHolder.members = view.findViewById(R.id.members);


        view.setTag(viewHolder);

        // Задаём контент
        final ViewHolder holder = (ViewHolder) view.getTag();


        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Runnable toRun = new Runnable() {
                    @Override
                    public void run() {
                        if (info.isHas()) {
                            Intent intent = new Intent(getContext(), Conversation.class);
                            intent.putExtra("cid", String.valueOf(info.getCid()));
                            intent.putExtra("isd", false);
                            intent.putExtra("name", info.getName());
                            intent.putExtra("cover", "null");
                            getContext().startActivity(intent);
                        } else {


                        }
                    }
                };

                Runnable cancel = new Runnable() {
                    @Override
                    public void run() {

                    }
                };

                AlertDialog cdd = new AlertDialog(context, getContext().getResources().getString(R.string.chat_join_title).replace("%s%", info.getName()), getContext().getResources().getString(R.string.chat_join_text), toRun, cancel);
                cdd.show();
            }
        });

        ImagesWorker.setGradient(holder.photo, info.getCid());
        holder.name.setText(info.getName());
        holder.members.setText(info.getMembers() + " " + view.getResources().getString(R.string.members));
        holder.acronym.setText(info.getName().substring(0, 1).toUpperCase());
        view.setTag(viewHolder);
        return view;
    }


    // Держим информацию
    static class ViewHolder {
        protected TextView name;
        protected TextView members;
        protected ImageView photo;
        protected TextView acronym;
    }
}