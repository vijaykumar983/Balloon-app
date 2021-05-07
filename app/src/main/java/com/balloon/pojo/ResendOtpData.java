package com.balloon.pojo;

import com.google.gson.annotations.SerializedName;

public class ResendOtpData {

    @SerializedName("data")
    private Data data;

    @SerializedName("message")
    private String message;

    @SerializedName("statusCode")
    private int statusCode;

    public void setData(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

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


    public static class Data {

        @SerializedName("user_id")
        private String userId;

        @SerializedName("otp")
        private String otp;

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

        public String getOtp() {
            return otp;
        }
    }

}