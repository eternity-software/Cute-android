package ru.etysoft.cute.activities.dialogs;

public class DialogInfo {

    private String name;
    private String lastmessage;

    private boolean selected;

    public DialogInfo(String name, String lastmessage) {
        this.name = name;
        this.lastmessage = lastmessage;
    }

    public String getName() {
        return name;
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
