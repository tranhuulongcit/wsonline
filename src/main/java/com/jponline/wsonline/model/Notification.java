package com.jponline.wsonline.model;

/**
 * Notification payload send notification
 * Author: LongTH10
 */
public class Notification {
    private String message;
    private Object data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
