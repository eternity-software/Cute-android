package ru.etysoft.cute.activities.messages;

public class MessageInfo {

    private String name;
    private String message;
    private String subtext;
    private String id;
    private String photo;
    private int aid;
    private boolean read;
    private final boolean isMine;
    private final boolean isDialog;
    private final boolean isInfo;


    // Сообщение
    public MessageInfo(String id, String name, String message, boolean isMine, boolean isDialog, String subtext, boolean read, int aid, boolean isInfo, String photo) {
        this.read = read;
        this.id = id;
        this.aid = aid;
        this.subtext = subtext;
        this.name = name;
        this.message = message;
        this.isMine = isMine;
        this.isDialog = isDialog;
        this.isInfo = isInfo;
        this.photo = photo + "?size=50";
    }

    public String getPhoto() {
        return photo;
    }

    public boolean isInfo() {
        return isInfo;
    }

    public int getAid() {
        return aid;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
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

    public boolean isDialog() {
        return isDialog;
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
