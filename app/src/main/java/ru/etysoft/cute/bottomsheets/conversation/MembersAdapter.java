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
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cuteframework.methods.chat.ChatMember;

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
        viewHolder.role = view.findViewById(R.id.creator);

        // Задаём обработчик нажатия
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(getContext(), Profile.class);
                    intent.putExtra("id", info.getId());
                    getContext().startActivity(intent);
            }
        });
        view.setTag(viewHolder);


        // Задаём персональные значения
        MembersAdapter.ViewHolder holder = (MembersAdapter.ViewHolder) view.getTag();
        if (!info.getRole().equals(ChatMember.Types.CREATOR)) {
            holder.role.setVisibility(View.INVISIBLE);
        }
        holder.picture.setAcronym(info.getName());
        holder.picture.generateIdPicture(info.getId());
        holder.name.setText(info.getName());
        if (info.getPhoto().equals("null")) {

        }


        return view;
    }


    // Держим данные
    static class ViewHolder {
        protected TextView name;
        protected Avatar picture;
        protected ImageView role;
    }
}
