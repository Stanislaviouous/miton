package com.example.demo.models;

import java.util.ArrayList;

public class Admin extends User{
    public Admin(String id, Boolean isAdmin, String name, String login, String password, ArrayList<Chat> chatArrayList) {
        super(id, isAdmin, name, login, password, chatArrayList);
    }

}
