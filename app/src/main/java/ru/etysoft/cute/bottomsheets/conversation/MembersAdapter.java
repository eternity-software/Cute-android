package ru.etysoft.cute.bottomsheets.conversation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.Profile;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.transition.Transitions;
import ru.etysoft.cute.utils.CircleTransform;
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
        viewHolder.name = view.findViewById(R.id.label);
        viewHolder.picture = view.findViewById(R.id.icon);
        viewHolder.role = view.findViewById(R.id.creator);

        // Задаём обработчик нажатия
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Profile.class);
                intent.putExtra("id", info.getId());
                if(info.getPhoto() != null)
                {
                    intent.putExtra(Profile.AVATAR, info.getPhoto());
                }
                Bundle bundle = Transitions.makeOneViewTransition(viewHolder.picture, context, intent, getContext().getResources().getString(R.string.transition_profile));
                if(bundle == null)
                {
                    context.startActivity(intent);
                }
                else
                {
                    context.startActivity(intent, bundle);
                }
            }
        });
        view.setTag(viewHolder);


        // Задаём персональные значения
        MembersAdapter.ViewHolder holder = (MembersAdapter.ViewHolder) view.getTag();
        if (!info.getRole().equals(ChatMember.Types.CREATOR)) {
            holder.role.setVisibility(View.INVISIBLE);
        }
        holder.picture.showAnimate();
        holder.picture.setAcronym(info.getName(), Avatar.Size.SMALL);
        holder.picture.generateIdPicture(2);
        holder.name.setText(info.getName());
        if (!info.getPhoto().equals("null")) {
            Picasso.get().load(info.getPhoto()).transform(new CircleTransform()).into(
                            holder.picture.getPictureView());
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
