package com.example.demo.models;

import java.util.ArrayList;
import java.util.TreeSet;

public class Chat {
    public String id;
    public Boolean isDeleted;
    public ArrayList<String> messages;
    public TreeSet<String> setIdUsers;

    public Chat(String id, Boolean isDeleted, ArrayList<String> messages, TreeSet<String> setIdUsers) {
        this.id = id;
        this.isDeleted = isDeleted;
        this.messages = messages;
        this.setIdUsers = setIdUsers;
    }

}
