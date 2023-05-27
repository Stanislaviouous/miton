package com.example.demo.models;

import java.util.Collection;
import java.util.TreeSet;

public class Message extends ID {
    private Boolean deleted;
    private String text;
    private Long time;
    private TreeSet<String> usersIds;

    public Message(String id, Boolean deleted, String text, Long time, Collection<String> usersIds) {
        super(id);
        this.deleted = deleted;
        this.text = text;
        this.time = time;
        this.usersIds = (TreeSet<String>) usersIds;
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
