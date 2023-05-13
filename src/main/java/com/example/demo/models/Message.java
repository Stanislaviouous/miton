package com.example.demo.models;

import java.util.ArrayList;
import java.util.TreeSet;

public class Message {
    public String id;
    public Boolean isDeleted;
    public String text;
    public Long time;
    public String idUserAuthor;
    public String idUserRecipient;
    public TreeSet<String> setIdUsers;

    public Message(String id, Boolean isDeleted, String text, Long time, String idUserAuthor, String idUserRecipient, TreeSet<String> setIdUsers) {
        this.id = id;
        this.isDeleted = isDeleted;
        this.text = text;
        this.time = time;
        this.idUserAuthor = idUserAuthor;
        this.idUserRecipient = idUserRecipient;
        this.setIdUsers = setIdUsers;
    }
}
