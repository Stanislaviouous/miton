package com.example.demo.models;

import java.util.ArrayList;

public class User {
    public String id;
    public Boolean isAdmin;
    public String name;
    public String login;
    public String password;
    public ArrayList<String> chatArrayList = new ArrayList<>();

    public User(String id, Boolean isAdmin, String name, String login, String password, ArrayList<String> chatArrayList) {
        this.id = id;
        this.isAdmin = isAdmin;
        this.name = name;
        this.login = login;
        this.password = password;
        this.chatArrayList = chatArrayList;
    }

    public String getId() {
        return id;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<String> getChatArrayList() {
        return chatArrayList;
    }
}
