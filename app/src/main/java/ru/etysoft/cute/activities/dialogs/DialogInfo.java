package ru.etysoft.cute.activities.dialogs;

public class DialogInfo {

    private String name;
    private String lastmessage;
    private String acronym;
    private String cid;

    public DialogInfo(String name, String lastmessage, String acronym, String cid) {
        this.name = name;
        this.lastmessage = lastmessage;
        this.acronym = acronym.toUpperCase();
        this.cid = cid;
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
