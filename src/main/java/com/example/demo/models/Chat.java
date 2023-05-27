package com.example.demo.models;

import java.util.ArrayList;

public class Chat extends ID {
    private Boolean deleted;
    private ArrayList<String> messagesIds;
    private ArrayList<String> usersIds;

    public Chat(String id, ArrayList<String> usersIds) {
        super(id);
        this.deleted = false;
        this.messagesIds = new ArrayList<String>();
        this.usersIds = usersIds;
    }

    public void addMessage(Message message) {
        this.messagesIds.add(message.id);
    }

    /**
     * @return Boolean return the isDeleted
     */
    public Boolean isDeleted() {
        return deleted;
    }

    /**
     * @param isDeleted the isDeleted to set
     */
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * @return ArrayList<String> return the messagesIds
     */
    public ArrayList<String> getMessagesIds() {
        return messagesIds;
    }

    /**
     * @param messagesIds the messagesIds to set
     */
    public void setMessagesIds(ArrayList<String> messagesIds) {
        this.messagesIds = messagesIds;
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

    public void deleteMessage(String messageId) {
        this.messagesIds.remove(messageId);
    }
}
