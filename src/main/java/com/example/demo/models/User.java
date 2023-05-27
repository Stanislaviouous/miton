package com.example.demo.models;

import java.util.ArrayList;

public class User extends ID {
    private Boolean admin;
    private String name;
    private String password;
    private ArrayList<String> chatsIds;

    public User(String id, Boolean admin, String name, String password) {
        super(id);
        this.admin = admin;
        this.name = name;
        this.password = password;
        this.chatsIds = new ArrayList<String>();
    }

    public void addChat(Chat chat) {
        this.chatsIds.add(chat.id);
    }

    /**
     * @return Boolean return the admin
     */
    public Boolean isAdmin() {
        return admin;
    }

    /**
     * @param admin the admin to set
     */
    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    /**
     * @return String return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return String return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return ArrayList<String> return the chatsIds
     */
    public ArrayList<String> getChatsIds() {
        return chatsIds;
    }

    /**
     * @param chatsIds the chatsIds to set
     */
    public void setChatsIds(ArrayList<String> chatsIds) {
        this.chatsIds = chatsIds;
    }

}
