package ru.etysoft.cute.activities.dialogs;

public class DialogInfo {

    private String name;
    private String lastmessage;
    private String acronym;
    private String cid;
    private String time;
    private String countMembers;
    private String cover;
    private boolean isreaded;
    private int countReaded;
    private boolean isOnline;
    private boolean isDialog;


    // Диалог или беседа (превью)
    public DialogInfo(String name, String lastmessage, String acronym, String cid, String time, boolean isreaded, int count, boolean isOnline, boolean isDialog, String countMembers, String cover) {
        this.name = name;
        this.lastmessage = lastmessage;
        this.acronym = acronym.toUpperCase();
        this.cid = cid;
        this.time = time;
        this.isreaded = isreaded;
        this.countReaded = count;
        this.isOnline = isOnline;
        this.isDialog = isDialog;
        this.countMembers = countMembers;
        this.cover = cover;
    }

    public String getCover() {
        return cover;
    }

    public String getCountMembers() {
        return countMembers;
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


    public int getCountReaded() {
        return countReaded;
    }

    public void setCountReaded(int count) {
        countReaded = count;
    }

    public boolean isReaded() {
        return isreaded;
    }

    public void isReaded(boolean isreaded) {
        this.isreaded = isreaded;
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

    public String getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(String message) {
        this.lastmessage = message;
    }


}
