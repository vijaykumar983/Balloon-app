package com.balloon.pojo;

import com.google.gson.annotations.SerializedName;

public class SendBalloonData {

    @SerializedName("message")
    private String message;

    @SerializedName("statusCode")
    private int statusCode;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}