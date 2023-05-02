package com.example.demo.models;

import java.util.ArrayList;

public class Message {
    String id;
    Boolean isDeleted;
    String text;
    Long time;
    String idUserAuthor;
    String idUserRecipient;

    public Message(String id, Boolean isDeleted, String text, Long time, String idUserAuthor, String idUserRecipient) {
        this.id = id;
        this.isDeleted = isDeleted;
        this.text = text;
        this.time = time;
        this.idUserAuthor = idUserAuthor;
        this.idUserRecipient = idUserRecipient;
    }
}
