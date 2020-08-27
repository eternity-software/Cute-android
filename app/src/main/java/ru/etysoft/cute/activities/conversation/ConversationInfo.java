package ru.etysoft.cute.activities.conversation;

public class ConversationInfo {

    private String name;
    private String message;
    private String subtext;
    private String id;
    private boolean isMine;
    private boolean isConversation;

    public ConversationInfo(String id, String name, String message, boolean isMine, boolean isConversation, String subtext) {
        this.id = id;
        this.subtext = subtext;
        this.name = name;
        this.message = message;
        this.isMine = isMine;
        this.isConversation = isConversation;
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

    public String getLastmessage() {
        return message;
    }

    public void setLastmessage(String message) {
        this.message = message;
    }
}
