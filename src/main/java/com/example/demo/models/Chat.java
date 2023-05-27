package com.example.demo.models;

import java.util.ArrayList;
import java.util.TreeSet;

public class Chat extends ID {
    private Boolean deleted;
    private ArrayList<String> messagesIds;
    private TreeSet<String> usersIds;

    public Chat(String id, TreeSet<String> usersIds) {
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
     * @return TreeSet<String> return the usersIds
     */
    public TreeSet<String> getUsersIds() {
        return usersIds;
    }

    /**
     * @param usersIds the usersIds to set
     */
    public void setUsersIds(TreeSet<String> usersIds) {
        this.usersIds = usersIds;
    }

}
