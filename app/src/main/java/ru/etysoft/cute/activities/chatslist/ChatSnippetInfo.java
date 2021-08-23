package ru.etysoft.cute.activities.chatslist;

public class ChatSnippetInfo {

    private String name;
    private String lastMessage;
    private String acronym;
    private String cid;
    private String time;
    private String cover;
    private boolean isRead;
    private int countRead;
    private boolean isOnline;
    private String senderName;
    private boolean isDialog;

    public ChatSnippetInfo(String name, String lastMessage, String senderName, String acronym, String cid, String time, boolean isRead,
                           int count, boolean isOnline, boolean isDialog, String cover) {
        this.name = name;
        this.lastMessage = lastMessage;
        this.acronym = acronym.toUpperCase();
        this.cid = cid;
        this.time = time;
        this.isRead = isRead;
        this.countRead = count;
        this.isOnline = isOnline;
        this.isDialog = isDialog;
        this.senderName = senderName;

        this.cover = cover;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getCover() {
        return cover;
    }


    public boolean isDialog() {
        return isDialog;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void isOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public int getCountRead() {
        return countRead;
    }

    public void setCountRead(int count) {
        countRead = count;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isreaded) {
        this.isRead = isreaded;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCid() {
        return cid;
    }

    public String getAcronym() {
        return acronym;
    }

    public String getName() {
        return name;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String message) {
        this.lastMessage = message;
    }


}
