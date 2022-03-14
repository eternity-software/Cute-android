package ru.etysoft.cute.activities.messaging;

import ru.etysoft.cuteframework.models.messages.Message;

public class MessageComponent {

    public static final int TYPE_MY_MESSAGE = 0;
    public static final int TYPE_DIALOG_MESSAGE = 2;
    public static final int TYPE_CHAT_ANNOTATION_MESSAGE = 3;
    public static final int TYPE_CHAT_MESSAGE = 4;
    public static final int TYPE_SERVICE_MESSAGE = 5;
    public static final int TYPE_DATE = 1;

    public static final int STATE_SENT = 0;
    public static final int STATE_PENDING = 1;
    public static final int STATE_ERROR = 2;

    private Message message;
    private int type;
    private int state = STATE_SENT;

    public MessageComponent(int type) {
        this.type = type;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public int getState() {
        return state;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
