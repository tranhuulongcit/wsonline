package com.jponline.wsonline.model;


/**
 * Message payload send message
 * Author: LongTH10
 */
public class Message {

    private MessageType type;
    private String content;
    private String sender;
    private String fname;
    private String fpath;
    private String toUser;
    private boolean isFist = true;

    public enum MessageType {
        CHAT, JOIN, LEAVE, FILE
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getFpath() {
        return fpath;
    }

    public void setFpath(String fpath) {
        this.fpath = fpath;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public boolean isFist() {
        return isFist;
    }

    public void setFist(boolean fist) {
        isFist = fist;
    }
}