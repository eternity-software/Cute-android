package ru.etysoft.cute.activities.messaging;

import ru.etysoft.cuteframework.models.messages.Message;

public class MessageComponent {

    public static final int TYPE_MY_MESSAGE = 0;
    public static final int TYPE_DATE = 1;

    private Message message;
    private int type;

    public MessageComponent(int type) {
        this.type = type;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }
}
