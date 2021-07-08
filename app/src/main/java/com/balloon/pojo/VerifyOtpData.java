package com.balloon.pojo;

import com.google.gson.annotations.SerializedName;

public class VerifyOtpData {

    @SerializedName("UserData")
    private UserData userData;

    @SerializedName("message")
    private String message;

    @SerializedName("isSend")
    private int isSend;

    @SerializedName("statusCode")
    private int statusCode;

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setIsSend(int isSend) {
        this.isSend = isSend;
    }

    public int getIsSend() {
        return isSend;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }


    public static class UserData {

        @SerializedName("firebase_id")
        private String firebaseId;

        @SerializedName("phone")
        private String phone;

        @SerializedName("name")
        private String name;

        @SerializedName("location")
        private String location;

        @SerializedName("profileImage")
        private String profileImage;

        @SerializedName("userId")
        private String userId;

        @SerializedName("deviceID")
        private String deviceID;

        public void setFirebaseId(String firebaseId){
            this.firebaseId = firebaseId;
        }

        public String getFirebaseId(){
            return firebaseId;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPhone() {
            return phone;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getLocation() {
            return location;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }

        public void setDeviceID(String deviceID) {
            this.deviceID = deviceID;
        }

        public String getDeviceID() {
            return deviceID;
        }
    }

}