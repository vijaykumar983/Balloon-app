package com.balloon.pojo;

public class Messages {

    private String message;
    private long time;
    private String type;
    private String from;
    private String user;

    public Messages(){

    }

    public Messages(String message, long time, String type, String from,String user) {
        this.message = message;
        this.time = time;
        this.type = type;
        this.from = from;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
