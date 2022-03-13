package ru.etysoft.cute.activities.messaging;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.utils.StringFormatter;
import ru.etysoft.cuteframework.models.messages.SuperMessage;

public class MessagesAdapter extends RecyclerView.Adapter<MessageViewHolder> {


    private List<MessageComponent> itemList = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public MessagesAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);

    }




    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).getType();
    }

    public List<MessageComponent> getItemList() {
        return itemList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageViewHolder viewHolder = new MessageViewHolder(layoutInflater.inflate(R.layout.info_message, parent, false));
        if (viewType == MessageComponent.TYPE_MY_MESSAGE) {
            viewHolder = new MessageViewHolder(layoutInflater.inflate(R.layout.conv_mymessage, parent, false));
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageComponent messageComponent = itemList.get(position);

        if (messageComponent.getType() == MessageComponent.TYPE_MY_MESSAGE) {
            if(messageComponent.getMessage() instanceof  SuperMessage)
            {
                SuperMessage superMessage = (SuperMessage) messageComponent.getMessage();
                View view = holder.getMessageView();

                TextView messageBody = view.findViewById(R.id.messageBody);
                TextView emojiView = view.findViewById(R.id.emojiView);

                String messageText = superMessage.getText();

                if(StringFormatter.isEmoji(messageText) && messageText.length() < 10)
                {
                    messageBody.setText("");
                    emojiView.setText(superMessage.getText());
                }
                else
                {
                    messageBody.setText(superMessage.getText());
                    emojiView.setText("");
                }


            }

        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
