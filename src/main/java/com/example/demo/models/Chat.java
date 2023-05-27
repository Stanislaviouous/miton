package com.example.demo.models;

import java.util.ArrayList;
import java.util.TreeSet;

public class Chat extends ID{
    public Boolean isDeleted;
    public ArrayList<String> messages;
    public TreeSet<String> parentUsers;

    public Chat(String id, Boolean isDeleted, ArrayList<String> messages, TreeSet<String> parentUsers) {
        super(id);
        this.isDeleted = isDeleted;
        this.messages = messages;
        this.parentUsers = parentUsers;
    }

}
