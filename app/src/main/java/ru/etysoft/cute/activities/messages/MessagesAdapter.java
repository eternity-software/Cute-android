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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.ImagePreview;
import ru.etysoft.cute.activities.Profile;
import ru.etysoft.cute.components.Attachments;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.lang.StringsRepository;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cute.utils.Numbers;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.chat.ServiceData;
import ru.etysoft.cuteframework.methods.messages.AttachmentData;
import ru.etysoft.cuteframework.methods.messages.Message;

public class MessagesAdapter extends ArrayAdapter<Message> {
    private final Activity context;
    private final List<Message> list;
    private boolean isDialog;

    public MessagesAdapter(Activity context, List<Message> values, boolean isDialog) {
        super(context, R.layout.dialog_element, values);
        this.context = context;
        this.list = values;
        this.isDialog = isDialog;
    }


    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View view = null;

        // Получение экземпляра сообщения по позиции
        final Message info = list.get(position);


        final LayoutInflater inflator = context.getLayoutInflater();
        boolean isFirstAid = false;
        boolean isService = false;

        // Проверка на беседу
        if (!isDialog) {
            // Проверка на своё сообщение
            boolean isMine = false;
            try {

                isMine = (info.getSender().getId() == Integer.parseInt(CachedValues.getId(context)));
                if (info.getId() >= 0) {
                    isService = info.getType().equals(Message.Type.SERVICE);
                } else {
                    isService = false;
                }
            } catch (Exception ignored) {

            }


            if (isMine) {
                isFirstAid = false;
                view = inflator.inflate(R.layout.conv_mymessage, null);
            } else if (info.getType().equals(Message.Type.SERVICE)) {
                view = inflator.inflate(R.layout.info_message, null);
            } else {
                if (position == 0) {
                    isFirstAid = true;
                    view = inflator.inflate(R.layout.chat_message, null);
                } else {
                    if (list.get(position - 1).getSender().getId() != info.getSender().getId() | list.get(position - 1).getType().equals(Message.Type.SERVICE)) {
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

        if (!isService) {
            // Не информационное сообщение
            viewHolder.attachments = view.findViewById(R.id.attachments);
            viewHolder.time = view.findViewById(R.id.timeview);
            viewHolder.message = view.findViewById(R.id.message_body);
            viewHolder.state = view.findViewById(R.id.state);
            viewHolder.back = view.findViewById(R.id.messageback);
            if (isFirstAid) {
                viewHolder.name = view.findViewById(R.id.nickname);
                viewHolder.userpic = view.findViewById(R.id.userpic);
            }

            view.setTag(viewHolder);

            // Задаём контент
            final ViewHolder holder = (ViewHolder) view.getTag();
            if (info.getId() > 0) {
                if(holder.state != null)
                {
                    holder.state.setVisibility(View.GONE);
                }
                if (info.isRead()) {
                    holder.back.setBackgroundColor(context.getResources().getColor(R.color.colorNotReaded));
                } else {
                    holder.back.setBackgroundColor(context.getResources().getColor(R.color.colorBackground));
                }


                AttachmentData attachmentData = info.getAttachmentData();
                if (attachmentData != null) {
                    Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                    Bitmap bmp = Bitmap.createBitmap(attachmentData.getWidth(), attachmentData.getHeight(), conf); // this creates a MUTABLE bitmap

                    bmp.eraseColor(context.getResources().getColor(R.color.colorPlaceholder));
                    holder.attachments.getImageView().setImageBitmap(bmp);

                    Drawable drawable = new BitmapDrawable(context.getResources(), bmp);
                    Picasso.get().load(info.getAttachmentPath()).placeholder(drawable).into(holder.attachments.getImageView());
                    holder.attachments.getImageView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), ImagePreview.class);
                            intent.putExtra("url", info.getAttachmentPath());
                            context.startActivity(intent);
                        }
                    });
                    holder.attachments.setVisibility(View.VISIBLE);
                }


                if (isFirstAid) {
                    holder.userpic.setAcronym(info.getSender().getDisplayName(), Avatar.Size.SMALL);
                    holder.name.setText(info.getSender().getDisplayName());
                    holder.userpic.generateIdPicture((int) info.getSender().getId());
                    if (info.getSender().getAvatar() != null) {
                        Picasso.get().load(info.getSender().getAvatar()).transform(new CircleTransform()).into(holder.userpic.getPictureView());
                    }
                    holder.userpic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), Profile.class);
                            intent.putExtra("id", info.getSender().getId());
                            getContext().startActivity(intent);
                        }
                    });

                }

                if(holder.time != null)
                {
                    holder.time.setText(String.valueOf(Numbers.getTimeFromTimestamp(info.getTime() + "000", context)));
                }

            } else if (info.getId() == -2) {
                holder.state.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_error));
            }
            if(holder.message != null)
            {
                holder.message.setText(info.getText());

            }

        } else {
            String messageText = "";
            try {
            ServiceData serviceData = info.getServiceData();
            if (serviceData.getType().equals(ServiceData.Types.CHAT_CREATED)) {

                    messageText = StringsRepository.getOrDefault(R.string.chat_created, getContext())
                            .replace("%s", serviceData.getChatName());

            } else if (serviceData.getType().equals(ServiceData.Types.ADD_MEMBER)) {
                messageText = StringsRepository.getOrDefault(R.string.add_member, getContext())
                        .replace("%s", serviceData.getDisplayName());
            } else {
                messageText = info.getText();
            }
            } catch (ResponseException e) {
                e.printStackTrace();
            }
            viewHolder.message = view.findViewById(R.id.infotext);
            view.setTag(viewHolder);
            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.message.setText(messageText);
        }

        return view;
    }


    // Держим информацию
    static class ViewHolder {
        protected TextView time;
        protected TextView message;
        protected ImageView state;
        protected RelativeLayout back;
        protected TextView name;
        protected Avatar userpic;
        protected Attachments attachments;
    }
}
