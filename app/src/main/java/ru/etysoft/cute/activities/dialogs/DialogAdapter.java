package ru.etysoft.cute.activities.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.Conversation;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cute.utils.ImagesWorker;

public class DialogAdapter extends ArrayAdapter<DialogInfo> {
    private final Activity context;
    private final List<DialogInfo> list;
    public static boolean canOpen = true;

    public DialogAdapter(Activity context, List<DialogInfo> values) {
        super(context, R.layout.dialog_element, values);
        this.context = context;
        this.list = values;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View view = null;

        // Инициализируем информацию о беседе или диалоге
        final DialogInfo info = list.get(position);

        final LayoutInflater inflator = context.getLayoutInflater();

        view = inflator.inflate(R.layout.dialog_element, null);


        final ViewHolder viewHolder = new ViewHolder();

        // Инициализируем подэлементы
        viewHolder.name = (TextView) view.findViewById(R.id.label);
        viewHolder.message = (TextView) view.findViewById(R.id.message);
        viewHolder.acronym = (TextView) view.findViewById(R.id.acronym);
        viewHolder.time = (TextView) view.findViewById(R.id.time);
        viewHolder.readstatus = (TextView) view.findViewById(R.id.readed);
        viewHolder.read = view.findViewById(R.id.icnread);
        viewHolder.online = view.findViewById(R.id.status);
        viewHolder.picture = view.findViewById(R.id.icon);

        // Задаём обработчик нажатия
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canOpen) {
                    canOpen = false;
                    Intent intent = new Intent(getContext(), Conversation.class);
                    intent.putExtra("cid", info.getCid());
                    intent.putExtra("isd", info.isDialog());
                    intent.putExtra("name", info.getName());
                    intent.putExtra("cover", info.getCover());
                    intent.putExtra("countMembers", info.getCountMembers());
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

        if (!info.isReaded()) {

            holder.read.setColorFilter(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.read.setColorFilter(context.getResources().getColor(R.color.colorBackgroundElements));
        }
        if (info.getCountReaded() == 0) {

            holder.readstatus.setVisibility(View.INVISIBLE);
        } else {
            holder.readstatus.setVisibility(View.VISIBLE);
            holder.readstatus.setText(String.valueOf(info.getCountReaded()));
        }

        if (info.getCover().equals("null")) {
            ImagesWorker.setGradient(holder.picture, Integer.parseInt(info.getCid()));
        } else {
            String photoUrl = Methods.getPhotoUrl(info.getCover()) + "?size=150";
            Picasso.get().load(photoUrl).placeholder(context.getResources().getDrawable(R.drawable.circle_gray)).transform(new CircleTransform()).into(holder.picture);
            holder.acronym.setVisibility(View.INVISIBLE);
        }

        holder.name.setText(info.getName());
        holder.message.setText(info.getLastmessage());
        holder.acronym.setText(info.getAcronym());
        holder.time.setText(info.getTime());

        return view;
    }


    // Держим данные
    static class ViewHolder {
        protected TextView name;
        protected TextView message;
        protected TextView acronym;
        protected TextView time;
        protected TextView readstatus;
        protected ImageView picture;
        protected ImageView read;
        protected ImageView online;
    }
}
