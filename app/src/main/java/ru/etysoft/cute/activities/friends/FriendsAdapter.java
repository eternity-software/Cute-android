package ru.etysoft.cute.activities.friends;

import android.app.Activity;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.MessagingActivity;
import ru.etysoft.cute.activities.Profile;
import ru.etysoft.cute.activities.chatslist.ChatsListAdapter;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.lang.StringsRepository;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cute.utils.Numbers;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.chat.ChatSnippet;
import ru.etysoft.cuteframework.methods.chat.ServiceData;
import ru.etysoft.cuteframework.methods.friend.Friend;
import ru.etysoft.cuteframework.methods.messages.Message;
import ru.etysoft.cuteframework.methods.user.User;

public class FriendsAdapter extends ArrayAdapter<User> {
    private final Activity context;
    private final List<User> list;
    public static boolean canOpen = true;

    public FriendsAdapter(Activity context, List<User> values) {
        super(context, R.layout.friend_element, values);
        this.context = context;
        this.list = values;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View view = null;

        // Инициализируем информацию о беседе или диалоге
        final User info = list.get(position);

        final LayoutInflater inflator = context.getLayoutInflater();

        view = inflator.inflate(R.layout.friend_element, null);


        final FriendsAdapter.ViewHolder viewHolder = new FriendsAdapter.ViewHolder();

        // Инициализируем подэлементы
        viewHolder.name = view.findViewById(R.id.displayNameView);

        viewHolder.avatar = view.findViewById(R.id.avatarView);
        viewHolder.container = view.findViewById(R.id.container);


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
        FriendsAdapter.ViewHolder holder = (FriendsAdapter.ViewHolder) view.getTag();


        if (holder.avatar != null) {
            holder.avatar.generateIdPicture((int) info.getId());
            holder.avatar.setAcronym(info.getDisplayName(), Avatar.Size.SMALL);
            if (info.getAvatar() != null) {
                Picasso.get().load(info.getAvatar()).transform(new CircleTransform()).into(holder.avatar.getPictureView());
            }
        }


        holder.name.setText(info.getDisplayName());




        return view;
    }


    // Держим данные
    static class ViewHolder {
        protected TextView name;
        protected Avatar avatar;
        protected LinearLayout container;
    }
}