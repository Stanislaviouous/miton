package com.example.demo.models;

import java.util.ArrayList;

public class Message extends ID {
    private Boolean deleted;
    private String text;
    private Long time;
    private String chatId;
    private ArrayList<String> usersIds;

    public Message(String id, Boolean deleted, String text, Long time, String chatId, ArrayList<String> usersIds) {
        super(id);
        this.deleted = deleted;
        this.text = text;
        this.time = time;
        this.chatId = chatId;
        this.usersIds = (ArrayList<String>) usersIds;
    }

    /**
     * @return Boolean return the deleted
     */
    public Boolean isDeleted() {
        return deleted;
    }

    /**
     * @param deleted the deleted to set
     */
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * @return String return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return Long return the time
     */
    public Long getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(Long time) {
        this.time = time;
    }

    /**
     * @return ArrayList<String> return the usersIds
     */
    public ArrayList<String> getUsersIds() {
        return usersIds;
    }

    /**
     * @param usersIds the usersIds to set
     */
    public void setUsersIds(ArrayList<String> usersIds) {
        this.usersIds = usersIds;
    }

    /**
     * @return String return the chatId
     */
    public String getChatId() {
        return chatId;
    }

    /**
     * @param chatId the chatId to set
     */
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

}
