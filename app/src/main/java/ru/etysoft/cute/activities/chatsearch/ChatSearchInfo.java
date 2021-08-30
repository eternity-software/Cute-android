package ru.etysoft.cute.activities.chatsearch;

public class ChatSearchInfo {

    private final int cid;
    private final int members;
    private final String name;
    private final boolean has;

    public ChatSearchInfo(int cid, String name, int members, boolean has) {
        this.cid = cid;
        this.name = name;
        this.members = members;
        this.has = has;
    }

    public boolean isHas() {
        return has;
    }

    public int getCid() {
        return cid;
    }

    public String getName() {
        return name;
    }

    public int getMembers() {
        return members;
    }
}
