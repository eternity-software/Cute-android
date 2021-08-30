package ru.etysoft.cute.activities.fragments.account;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.Profile;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cuteframework.methods.friend.Friend;

public class FriendsSnippetAdapter extends RecyclerView.Adapter<FriendsSnippetAdapter.ViewHolder>{

    private final LayoutInflater inflater;
    private final List<Friend> friends;

    FriendsSnippetAdapter(Context context, List<Friend> friends) {
        this.friends = friends;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.friend_snippet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Friend friend = friends.get(position);
        holder.avatar.setAcronym(friend.getDisplayName(), Avatar.Size.SMALL);
        holder.avatar.generateIdPicture(friend.getAccountId());
        holder.avatar.setOnline(true);
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inflater.getContext(), Profile.class);
                intent.putExtra("id", friend.getAccountId());
                inflater.getContext().startActivity(intent);
            }
        });
        Picasso.get().load(friend.getAvatarPath()).transform(new CircleTransform()).into(holder.avatar.getPictureView());
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final Avatar avatar;

        ViewHolder(View view){
            super(view);
            avatar = view.findViewById(R.id.avatarView);
        }
    }
}
