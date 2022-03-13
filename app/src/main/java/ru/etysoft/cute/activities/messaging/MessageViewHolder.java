package ru.etysoft.cute.activities.messaging;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private View messageView;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        messageView = itemView;
    }

    public View getMessageView() {
        return messageView;
    }
}
