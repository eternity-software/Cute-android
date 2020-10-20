package ru.etysoft.cute.activities.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.Conversation;

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

            holder.read.setColorFilter(context.getResources().getColor(R.color.colorMain));
        } else {
            holder.read.setColorFilter(context.getResources().getColor(R.color.colorBackgroundElements));
        }
        if (info.getCountReaded() == 0) {

            holder.readstatus.setVisibility(View.INVISIBLE);
        } else {
            holder.readstatus.setVisibility(View.VISIBLE);
            holder.readstatus.setText(String.valueOf(info.getCountReaded()));
        }

        GradientDrawable gd = new GradientDrawable();

        int color1 = Color.parseColor("#B83ADE");
        int color2 = Color.parseColor("#B83ADE");
        int id = Integer.parseInt(info.getCid());
        String sid = String.valueOf(info.getCid());
        Log.d("ID", "cur= " + id);


        color1 = this.context.getResources().getColor(R.color.colorMain);
        switch (String.valueOf(sid.charAt(sid.length() - 1))) {
            case ("1"):
                color1 = Color.parseColor("#21a2b0");
                break;
            case ("2"):
                color1 = Color.parseColor("#53b053");
                break;
            case ("3"):
                color1 = Color.parseColor("#53b053");
                break;
            case ("4"):
                color1 = Color.parseColor("#9ca82d");
                break;
            case ("5"):
                color1 = Color.parseColor("#1b7ca6");
                break;
            case ("6"):
                color1 = Color.parseColor("#6e3dd1");
                break;
            case ("7"):
                color1 = Color.parseColor("#3dd15d");
                break;
            case ("8"):
                color1 = Color.parseColor("#d1693d");
                break;
            case ("9"):
                color1 = Color.parseColor("#3d3dd1");
                break;
            case ("0"):
                color1 = Color.parseColor("#B83ADE");
                break;
        }

        if (id > 9) {
            switch (String.valueOf(sid.charAt(sid.length() - 2))) {
                case ("1"):
                    color2 = Color.parseColor("#21a2b0");
                    break;
                case ("2"):
                    color2 = Color.parseColor("#53b053");
                    break;
                case ("3"):
                    color2 = Color.parseColor("#53b053");
                    break;
                case ("4"):
                    color2 = Color.parseColor("#9ca82d");
                    break;
                case ("5"):
                    color2 = Color.parseColor("#1b7ca6");
                    break;
                case ("6"):
                    color2 = Color.parseColor("#6e3dd1");
                    break;
                case ("7"):
                    color2 = Color.parseColor("#3dd15d");
                    break;
                case ("8"):
                    color2 = Color.parseColor("#d1693d");
                    break;
                case ("9"):
                    color2 = Color.parseColor("#B83ADE");
                    break;
                case ("0"):
                    color2 = Color.parseColor("#9e9ed9");
                    break;
            }
        }
        if (color1 == color2) {
            color1 = Color.parseColor("#6f299e");
        }

        // Set the color array to draw gradient
        gd.setColors(new int[]{
                color1,
                color2
        });

        // Set the GradientDrawable gradient type linear gradient
        gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        // Set GradientDrawable shape is a rectangle
        gd.setShape(GradientDrawable.OVAL);


        // Set 3 pixels width solid blue color border


        // Set GradientDrawable width and in pixels
        gd.setSize(100, 100); // Width 450 pixels and height 150 pixels

        // Set GradientDrawable as ImageView source image
        holder.picture.setImageDrawable(gd);

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
