package ru.etysoft.cute.activities.messages;

import ru.etysoft.cuteframework.methods.messages.AttachmentData;

public class MessageInfo {

    private String name;
    private String message;
    private String subtext;
    private String id;
    private final String avatar;
    private final String media;
    private final int aid;
    private boolean read;
    private final boolean isMine;
    private final boolean isDialog;
    private final boolean isInfo;
    private final AttachmentData attachmentData;


    // Сообщение
    public  MessageInfo(String id, String name, String message, boolean isMine, boolean isDialog, String subtext, boolean read, int aid, boolean isInfo, String avatar,
                        String media, AttachmentData attachmentData) {
        this.read = read;
        this.id = id;
        this.aid = aid;
        this.subtext = subtext;
        this.name = name;
        this.message = message;
        this.isMine = isMine;
        this.isDialog = isDialog;
        this.isInfo = isInfo;
        this.avatar = avatar;
        this.media = media;
        this.attachmentData = attachmentData;
    }

    public AttachmentData getAttachmentData() {
        return attachmentData;
    }

    public String getMedia() {
        return media;
    }

    public String getAvatar() {
        return avatar;
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
