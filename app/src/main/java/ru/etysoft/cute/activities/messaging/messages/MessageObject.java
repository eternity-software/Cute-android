package ru.etysoft.cute.activities.messaging.messages;

import android.widget.ListView;

import androidx.recyclerview.widget.RecyclerView;

import ru.etysoft.cuteframework.methods.messages.Message;

public class MessageObject extends RecyclerObject {

    private Message message;

    public MessageObject(RecyclerView parent, Message message) {
        super(parent);
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
