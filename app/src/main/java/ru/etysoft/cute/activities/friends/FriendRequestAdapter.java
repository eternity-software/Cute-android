package ru.etysoft.cute.activities.friends;

import android.app.Activity;
import android.content.Intent;
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

import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;

import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.MessagingActivity;
import ru.etysoft.cute.activities.Profile;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cute.utils.SliderActivity;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.friend.AcceptRequest.AcceptFriendRequest;
import ru.etysoft.cuteframework.methods.friend.AcceptRequest.AcceptFriendResponse;
import ru.etysoft.cuteframework.methods.friend.DeclineRequest.DeclineFriendRequest;
import ru.etysoft.cuteframework.methods.friend.DeclineRequest.DeclineFriendResponse;
import ru.etysoft.cuteframework.methods.friend.Friend;

public class FriendRequestAdapter extends ArrayAdapter<Friend> {
    private final Activity context;
    private final List<Friend> list;
    private boolean isIncoming = true;

    public FriendRequestAdapter(Activity context, List<Friend> values) {
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
        final Friend info = list.get(position);

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
                intent.putExtra("id", info.getAccountId());
                getContext().startActivity(intent);
            }
        });

        view.setTag(viewHolder);

        // Задаём персональные значения
        FriendRequestAdapter.ViewHolder holder = (FriendRequestAdapter.ViewHolder) view.getTag();


        if (holder.avatar != null) {

            holder.avatar.generateIdPicture(info.getAccountId());
            holder.avatar.setAcronym(info.getDisplayName(), Avatar.Size.SMALL);
            if (info.getAvatarPath() != null) {
                Picasso.get().load(info.getAvatarPath()).transform(new CircleTransform()).into(holder.avatar.getPictureView());
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
                            AcceptFriendResponse acceptFriendResponse = (new AcceptFriendRequest(CachedValues.getSessionKey(getContext()), String.valueOf(info.getRequestId()))).execute();
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
                            DeclineFriendResponse declineFriendResponse = (new DeclineFriendRequest(CachedValues.getSessionKey(getContext()), String.valueOf(info.getRequestId()))).execute();
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