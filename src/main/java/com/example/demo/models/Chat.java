package com.example.demo.models;

import java.util.ArrayList;
import java.util.TreeSet;

public class Chat {
    String id;
    Boolean isDeleted;
    ArrayList<String> messages;
    TreeSet<String> setIdUsers;

    public Chat(String id, Boolean isDeleted, ArrayList<String> messages, TreeSet<String> setIdUsers) {
        this.id = id;
        this.isDeleted = isDeleted;
        this.messages = messages;
        this.setIdUsers = setIdUsers;
    }
}
