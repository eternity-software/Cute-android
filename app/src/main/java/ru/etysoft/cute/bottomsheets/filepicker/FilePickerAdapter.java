package ru.etysoft.cute.bottomsheets.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.Profile;
import ru.etysoft.cute.bottomsheets.conversation.MemberInfo;
import ru.etysoft.cute.bottomsheets.conversation.MembersAdapter;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cuteframework.methods.chat.ChatMember;

public class FilePickerAdapter extends ArrayAdapter<FileInfo> {
    public static boolean canOpen = true;
    private final Activity context;
    private final List<FileInfo> list;

    public FilePickerAdapter(Activity context, List<FileInfo> values) {
        super(context, R.layout.member_element, values);
        this.context = context;
        this.list = values;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View view = null;

        final FileInfo info = list.get(position);

        final LayoutInflater inflator = context.getLayoutInflater();
        view = inflator.inflate(R.layout.member_element, null);
        final FilePickerAdapter.ViewHolder viewHolder = new FilePickerAdapter.ViewHolder();


        viewHolder.name = (TextView) view.findViewById(R.id.label);
        viewHolder.picture = view.findViewById(R.id.icon);
        viewHolder.role = view.findViewById(R.id.creator);


        view.setTag(viewHolder);



        FilePickerAdapter.ViewHolder holder = (FilePickerAdapter.ViewHolder) view.getTag();
        holder.picture.setAcronym(info.getName(), Avatar.Size.SMALL);
        holder.name.setText(info.getName());



        return view;
    }


    // Держим данные
    static class ViewHolder {
        protected TextView name;
        protected Avatar picture;
        protected ImageView role;
    }
}
