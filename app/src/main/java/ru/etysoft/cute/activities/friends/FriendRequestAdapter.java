package ru.etysoft.cute.activities.friends;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.Profile;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cuteframework.exceptions.ResponseException;

import ru.etysoft.cuteframework.methods.friend.Remove.RemoveFriendRequest;
import ru.etysoft.cuteframework.methods.friend.Remove.RemoveFriendResponse;
import ru.etysoft.cuteframework.methods.friend.SendRequest.AddFriendRequest;
import ru.etysoft.cuteframework.methods.friend.SendRequest.AddFriendRequestResponse;
import ru.etysoft.cuteframework.methods.user.User;

public class FriendRequestAdapter extends ArrayAdapter<User> {
    private final Activity context;
    private final List<User> list;
    private boolean isIncoming = true;

    public FriendRequestAdapter(Activity context, List<User> values) {
        super(context, R.layout.friend_element, values);
        this.context = context;
        this.list = values;


    }

    public void setIncoming(boolean incoming) {
        isIncoming = incoming;
    }

    public boolean isIncoming() {
        return isIncoming;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View view = null;

        // Инициализируем информацию о беседе или диалоге
        final User info = list.get(position);

        final LayoutInflater inflator = context.getLayoutInflater();

        view = inflator.inflate(R.layout.request_element, null);


        final FriendRequestAdapter.ViewHolder viewHolder = new FriendRequestAdapter.ViewHolder();

        // Инициализируем подэлементы
        viewHolder.name = view.findViewById(R.id.displayNameView);

        viewHolder.avatar = view.findViewById(R.id.avatarView);
        viewHolder.container = view.findViewById(R.id.container);
        viewHolder.acceptButton = view.findViewById(R.id.acceptButton);
        viewHolder.declineButton = view.findViewById(R.id.declineButton);


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
        FriendRequestAdapter.ViewHolder holder = (FriendRequestAdapter.ViewHolder) view.getTag();


        if (holder.avatar != null) {

            holder.avatar.generateIdPicture((int) info.getId());
            holder.avatar.setAcronym(info.getDisplayName(), Avatar.Size.SMALL);
            if (info.getAvatar() != null) {
                Picasso.get().load(info.getAvatar()).transform(new CircleTransform()).into(holder.avatar.getPictureView());
            }
        }


        if(!isIncoming)
        {
            holder.acceptButton.setVisibility(View.GONE);
        }


        holder.name.setText(info.getDisplayName());



        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            AddFriendRequestResponse acceptFriendResponse = (new AddFriendRequest(CachedValues.getSessionKey(getContext()), String.valueOf(info.getId()))).execute();
                            if(acceptFriendResponse.isSuccess())
                            {
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        context.finish();
                                    }
                                });
                            }
                        } catch (ResponseException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });

        holder.declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            RemoveFriendResponse declineFriendResponse = (new RemoveFriendRequest(CachedValues.getSessionKey(getContext()), String.valueOf(info.getId()))).execute();
                            if(declineFriendResponse.isSuccess())
                            {
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        context.finish();
                                    }
                                });
                            }
                        } catch (ResponseException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });

        return view;
    }


    // Держим данные
    static class ViewHolder {
        protected TextView name;
        protected Avatar avatar;
        protected LinearLayout container;
        protected ImageView acceptButton;
        protected ImageView declineButton;
    }
}