package ru.etysoft.cute.activities.messages;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.graphics.drawable.DrawableCompat;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.ImagePreview;
import ru.etysoft.cute.activities.Profile;
import ru.etysoft.cute.components.Attachments;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cuteframework.methods.messages.AttachmentData;

public class MessagesAdapter extends ArrayAdapter<MessageInfo> {
    private final Activity context;
    private final List<MessageInfo> list;

    public MessagesAdapter(Activity context, List<MessageInfo> values) {
        super(context, R.layout.dialog_element, values);
        this.context = context;
        this.list = values;
    }


    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View view = null;

        // Получение экземпляра сообщения по позиции
        final MessageInfo info = list.get(position);


        final LayoutInflater inflator = context.getLayoutInflater();
        boolean isFirstAid = false;

        // Проверка на беседу
        if (!info.isDialog()) {
            // Проверка на своё сообщение
            if (info.isMine() && !info.isInfo()) {
                view = inflator.inflate(R.layout.conv_mymessage, null);
            } else if (info.isInfo() == true) {
                isFirstAid = false;
                view = inflator.inflate(R.layout.info_message, null);
            } else {
                if (position == 0) {
                    isFirstAid = true;
                    view = inflator.inflate(R.layout.chat_message, null);
                } else {
                    if (list.get(position - 1).getAid() != info.getAid() | list.get(position - 1).isInfo()) {
                        isFirstAid = true;
                        view = inflator.inflate(R.layout.chat_message, null);
                    } else {
                        view = inflator.inflate(R.layout.chat_messagenoinfo, null);
                    }
                }
            }

        } else {

        }


        // Инициализируем элементы
        final MessagesAdapter.ViewHolder viewHolder = new MessagesAdapter.ViewHolder();

        if (!info.isInfo()) {
            // Не информационное сообщение
            viewHolder.attachments = view.findViewById(R.id.attachments);
            viewHolder.time = view.findViewById(R.id.timeview);
            viewHolder.message = view.findViewById(R.id.message_body);
            viewHolder.back = view.findViewById(R.id.messageback);
            if (isFirstAid) {
                viewHolder.name = view.findViewById(R.id.nickname);
                viewHolder.userpic = view.findViewById(R.id.userpic);
            }

            view.setTag(viewHolder);

            // Задаём контент
            final ViewHolder holder = (ViewHolder) view.getTag();
            if (info.isRead()) {
                holder.back.setBackgroundColor(context.getResources().getColor(R.color.colorNotReaded));
            } else {
                holder.back.setBackgroundColor(context.getResources().getColor(R.color.colorBackground));
            }

            if(info.getMedia() != null)
            {
                AttachmentData attachmentData = info.getAttachmentData();
                if(attachmentData != null) {
                    Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                    Bitmap bmp = Bitmap.createBitmap(attachmentData.getWidth(), attachmentData.getHeight(), conf); // this creates a MUTABLE bitmap

                    bmp.eraseColor(context.getResources().getColor(R.color.colorPlaceholder));
                    holder.attachments.getImageView().setImageBitmap(bmp);

                    Drawable drawable = new BitmapDrawable(context.getResources(), bmp);
                    Picasso.get().load(info.getMedia()).placeholder(drawable).into(holder.attachments.getImageView());
                    holder.attachments.getImageView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), ImagePreview.class);
                            intent.putExtra("url", info.getMedia());
                            context.startActivity(intent);
                        }
                    });
                    holder.attachments.setVisibility(View.VISIBLE);
                }

            }

            if (isFirstAid) {
                holder.userpic.setAcronym(info.getName(), Avatar.Size.SMALL);
                holder.name.setText(info.getName());
                holder.userpic.generateIdPicture(info.getAid());
                if (info.getAvatar() != null) {
                    Picasso.get().load(info.getAvatar()).transform(new CircleTransform()).into(holder.userpic.getPictureView());
                }
                holder.userpic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), Profile.class);
                        intent.putExtra("id", info.getAid());
                        getContext().startActivity(intent);
                    }
                });

            }


            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    holder.message.setText("MESSAGE INFO: \n" +
                            "ID: " + info.getId() + "\n"
                            + "READED: " + info.isRead() + "\n"
                            + "MY: " + info.isMine() + "\n"
                            + "NAME: " + info.getName() + "\n"
                            + "AID: " + info.getAid() + "\n"
                            + "MEDIAD: " + info.getMedia()
                    );
                }
            });

            holder.time.setText(info.getSubtext());
            holder.message.setText(info.getMessage());
        } else {

            viewHolder.message = view.findViewById(R.id.infotext);
            view.setTag(viewHolder);
            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.message.setText(info.getMessage());
        }

        return view;
    }


    // Держим информацию
    static class ViewHolder {
        protected TextView time;
        protected TextView message;
        protected RelativeLayout back;
        protected TextView name;
        protected Avatar userpic;
        protected Attachments attachments;
    }
}
