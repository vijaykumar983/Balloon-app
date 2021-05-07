package com.balloon.pojo;

import com.google.gson.annotations.SerializedName;

public class LoginData {

    @SerializedName("data")
    private Data data;

    @SerializedName("message")
    private String message;

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("userStatus")
    private String userStatus;

    public void setData(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
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

        @SerializedName("UserData")
        private UserData userData;

        @SerializedName("otp")
        private int otp;

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserData(UserData userData) {
            this.userData = userData;
        }

        public UserData getUserData() {
            return userData;
        }

        public void setOtp(int otp) {
            this.otp = otp;
        }

        public int getOtp() {
            return otp;
        }


        public static class UserData {

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

}