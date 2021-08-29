package ru.etysoft.cute.activities.fragments.account;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.Profile;
import ru.etysoft.cute.activities.SettingsActivity;
import ru.etysoft.cute.activities.editprofile.EditProfileActivity;
import ru.etysoft.cute.activities.friends.FriendsActivity;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cuteframework.methods.friend.Friend;
import ru.etysoft.cuteframework.methods.friend.GetFriends.FriendListRequest;
import ru.etysoft.cuteframework.methods.friend.GetFriends.FriendListResponse;

public class AccountFragment extends Fragment implements AccountContact.View{
    private View view;

    private ImageView settingImageView;
    private ImageView editButton;
    private Avatar userimage;
    private TextView status;
    private TextView login;
    private LinearLayout friendsView;

    private List friends;

    private AccountContact.Presenter presenter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.activity_account, container, false);
        view = root;
        presenter = new AccountPresenter(getActivity(), this);
        initializeViews();
        presenter.updateData();
        friend();
        return root;
    }

    @Override
    public void initializeViews() {
        userimage = view.findViewById(R.id.userimage);
        status = view.findViewById(R.id.statusView);
        login = view.findViewById(R.id.login);
        settingImageView = view.findViewById(R.id.setting);
        friendsView = view.findViewById(R.id.friendsView);

        userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().openAvatar();
            }
        });

        settingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                getActivity().startActivity(intent);
            }
        });
        editButton = view.findViewById(R.id.editProfile);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra("name", "");
                intent.putExtra("profilePhoto", "status");
                intent.putExtra("status", status.getText());
                intent.putExtra("login", login.getText());
                startActivity(intent);
            }
        });

        friendsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendsActivity.class);
                getActivity().startActivity(intent);
            }
        });


    }

    public void friend()
    {
        Thread processServerRequest = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FriendListResponse friendListResponse = (new FriendListRequest(CachedValues.getSessionKey(getActivity()))).execute();
                    if(friendListResponse.isSuccess())
                    {
                        friends = friendListResponse.getFriends();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GridView recyclerView = (GridView) getActivity().findViewById(R.id.friendSnippets);

                                recyclerView.setAdapter(new FriendsSnippetsAdapter(getActivity()));
                            }
                        });
                    }
                }
                catch (Exception e)
                {

                }
            }
        });
        processServerRequest.start();
    }

    @Override
    public void setAccountInfo(String login, String status, String photo, int id) {
        this.status.setText(status);
        this.login.setText(login);
        userimage.generateIdPicture(id);
        userimage.setAcronym(login, Avatar.Size.LARGE);
        if(photo != null) {
            Picasso.get().load(photo).placeholder(getResources().getDrawable(R.drawable.circle_gray)).transform(new CircleTransform()).into(userimage.getPictureView());
        }
    }

    public AccountContact.Presenter getPresenter() {
        if(presenter == null) {
            presenter = new AccountPresenter(getActivity(), this);
        }
        return presenter;

    }

    private class FriendsSnippetsAdapter extends BaseAdapter {

        /** The context. */
        private Activity context;
        private int threadCount = 0;

        /**
         * Instantiates a new image adapter.
         *
         * @param localContext
         *            the local context
         */
        public FriendsSnippetsAdapter(Activity localContext) {
            context = localContext;

        }

        public int getCount() {
            return friends.size();
        }

        public Object getItem(int position) {
            return position;
        }


        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            final Friend friend = (Friend) friends.get(position);


            Avatar avatar = new Avatar(context);

            avatar.setAcronym(friend.getDisplayName(), Avatar.Size.MEDIUM);
            avatar
                    .setLayoutParams(new GridView.LayoutParams(180, 180));
            Picasso.get().load(friend.getAvatarPath()).transform(new CircleTransform()).into(avatar.getPictureView());
            avatar.generateIdPicture(friend.getAccountId());
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), Profile.class);
                    intent.putExtra("id", friend.getAccountId());
                    getContext().startActivity(intent);
                }
            });


            return avatar;
        }

        private Bitmap decodeFile(File f) throws Exception{
            Bitmap b = null;

            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;

            int MAX_SIZE = 500;

            int IMAGE_MAX_SIZE=Math.max(MAX_SIZE,MAX_SIZE);
            if (o.outHeight > MAX_SIZE || o.outWidth > MAX_SIZE) {
                scale = (int)Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                        (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();

            return b;
        }

        /**
         * Getting All Images Path.
         *
         * @param activity
         *            the activity
         * @return ArrayList with images Path
         */
        private ArrayList<String> getAllShownImagesPath(FrameLayout activity) {
            Uri uri;
            Cursor cursor;
            int column_index_data, column_index_folder_name;
            ArrayList<String> listOfAllImages = new ArrayList<String>();
            String absolutePathOfImage = null;
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = { MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

            cursor = activity.getContext().getContentResolver().query(uri, projection, null,
                    null, null);

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);

                listOfAllImages.add(absolutePathOfImage);
            }
            return listOfAllImages;
        }
    }
}