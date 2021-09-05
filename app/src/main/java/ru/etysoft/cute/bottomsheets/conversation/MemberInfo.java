package ru.etysoft.cute.bottomsheets.conversation;

public class MemberInfo {


    private long id;
    private String name;
    private String role;
    private final String photo;

    public MemberInfo(long id, String name, String role, String photo) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
