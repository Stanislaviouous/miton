package com.example.demo.models;

import java.util.ArrayList;

public class User extends ID {
    public Boolean isAdmin;
    public String name;
    public String password;
    public ArrayList<String> chats;

    public User(String id, Boolean isAdmin, String name, String password) {
        super(id);
        this.isAdmin = isAdmin;
        this.name = name;
        this.password = password;
        this.chats = new ArrayList<String>();
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<String> getChats() {
        return chats;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addChat(Chat chat) {
        this.chats.add(chat.id);
    }
}
