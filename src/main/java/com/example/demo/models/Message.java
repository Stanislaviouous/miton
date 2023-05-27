package com.example.demo.models;

public class Message extends ID {
    public Boolean isDeleted;
    public String text;
    public Long time;
    public String idUserAuthor;
    public String idUserRecipient;

    public Message(String id, Boolean isDeleted, String text, Long time, String idUserAuthor, String idUserRecipient) {
        super(id);
        this.isDeleted = isDeleted;
        this.text = text;
        this.time = time;
        this.idUserAuthor = idUserAuthor;
        this.idUserRecipient = idUserRecipient;
    }
}
