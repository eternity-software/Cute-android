package ru.etysoft.cute.bottomsheets.conversation;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.Profile;
import ru.etysoft.cute.utils.ImagesWorker;

public class MembersAdapter extends ArrayAdapter<MemberInfo> {
    public static boolean canOpen = true;
    private final Activity context;
    private final List<MemberInfo> list;

    public MembersAdapter(Activity context, List<MemberInfo> values) {
        super(context, R.layout.member_element, values);
        this.context = context;
        this.list = values;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View view = null;

        // Инициализируем информацию о беседе или диалоге
        final MemberInfo info = list.get(position);

        final LayoutInflater inflator = context.getLayoutInflater();
        view = inflator.inflate(R.layout.member_element, null);
        final MembersAdapter.ViewHolder viewHolder = new MembersAdapter.ViewHolder();

        // Инициализируем подэлементы
        viewHolder.name = (TextView) view.findViewById(R.id.label);
        viewHolder.picture = view.findViewById(R.id.icon);
        viewHolder.acronym = view.findViewById(R.id.acronym);
        viewHolder.role = view.findViewById(R.id.creator);

        // Задаём обработчик нажатия
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canOpen) {
                    canOpen = false;
                    Intent intent = new Intent(getContext(), Profile.class);
                    intent.putExtra("id", info.getId());
                    getContext().startActivity(intent);
                }
            }
        });
        view.setTag(viewHolder);

        // Задаём персональные значения
        MembersAdapter.ViewHolder holder = (MembersAdapter.ViewHolder) view.getTag();
        if (!info.getRole().equals("creator")) {
            holder.role.setVisibility(View.INVISIBLE);
        }
        ImagesWorker.setGradient(holder.picture, info.getId());
        holder.name.setText(info.getName());
        holder.acronym.setText(String.valueOf(info.getName().charAt(0)));

        return view;
    }


    // Держим данные
    static class ViewHolder {
        protected TextView name;
        protected TextView acronym;
        protected ImageView picture;
        protected ImageView role;
    }
}
