package ru.etysoft.cute.activities.conversation;

public class ConversationInfo {

    private String name;
    private String message;
    private String subtext;
    private String id;
    private int aid;
    private boolean readed;
    private boolean isMine;
    private boolean isConversation;
    private boolean isInfo;


    // Сообщение
    public ConversationInfo(String id, String name, String message, boolean isMine, boolean isConversation, String subtext, boolean readed, int aid, boolean isInfo) {
        this.readed = readed;
        this.id = id;
        this.aid = aid;
        this.subtext = subtext;
        this.name = name;
        this.message = message;
        this.isMine = isMine;
        this.isConversation = isConversation;
        this.isInfo = isInfo;
    }

    public boolean isInfo() {
        return isInfo;
    }

    public int getAid() {
        return aid;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubtext() {
        return subtext;
    }

    public void setSubtext(String text) {
        subtext = text;
    }

    public boolean isMine() {
        return isMine;
    }

    public boolean isConversation() {
        return isConversation;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
