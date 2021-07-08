package com.balloon.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatUserListData {

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

        @SerializedName("Bubbles")
        private Bubbles bubbles;

        @SerializedName("userId")
        private String userId;

        public void setBubbles(Bubbles bubbles) {
            this.bubbles = bubbles;
        }

        public Bubbles getBubbles() {
            return bubbles;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }


        public static class Bubbles {

            @SerializedName("new")
            private List<JsonMemberNewItem> jsonMemberNew;

            @SerializedName("chatUser")
            private List<ChatUserItem> chatUser;

            public void setJsonMemberNew(List<JsonMemberNewItem> jsonMemberNew) {
                this.jsonMemberNew = jsonMemberNew;
            }

            public List<JsonMemberNewItem> getJsonMemberNew() {
                return jsonMemberNew;
            }

            public void setChatUser(List<ChatUserItem> chatUser) {
                this.chatUser = chatUser;
            }

            public List<ChatUserItem> getChatUser() {
                return chatUser;
            }


            public static class JsonMemberNewItem {

                @SerializedName("image")
                private String image;

                @SerializedName("firebase_id")
                private String firebaseId;

                @SerializedName("id")
                private String id;

                @SerializedName("userId")
                private String userId;

                @SerializedName("status")
                private String status;

                @SerializedName("Name")
                private String name;

                public void setImage(String image) {
                    this.image = image;
                }

                public String getImage() {
                    return image;
                }

                public void setFirebaseId(String firebaseId) {
                    this.firebaseId = firebaseId;
                }

                public String getFirebaseId() {
                    return firebaseId;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getId() {
                    return id;
                }

                public void setUserId(String userId) {
                    this.userId = userId;
                }

                public String getUserId() {
                    return userId;
                }

                public void setStatus(String status) {
                    this.status = status;
                }

                public String getStatus() {
                    return status;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getName() {
                    return name;
                }
            }

            public static class ChatUserItem {

                @SerializedName("image")
                private String image;

                @SerializedName("firebase_id")
                private String firebaseId;

                @SerializedName("id")
                private String id;

                @SerializedName("userId")
                private String userId;

                @SerializedName("status")
                private String status;

                @SerializedName("isBlock")
                private String isBlock;

                @SerializedName("BlockStatus")
                private String blockStatus;

                @SerializedName("Name")
                private String name;

                @SerializedName("deviceId")
                private String deviceId;

                public void setDeviceId(String deviceId) {
                    this.deviceId = deviceId;
                }

                public String getDeviceId() {
                    return deviceId;
                }

                public void setImage(String image) {
                    this.image = image;
                }

                public String getImage() {
                    return image;
                }

                public void setFirebaseId(String firebaseId) {
                    this.firebaseId = firebaseId;
                }

                public String getFirebaseId() {
                    return firebaseId;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getId() {
                    return id;
                }

                public void setUserId(String userId) {
                    this.userId = userId;
                }

                public String getUserId() {
                    return userId;
                }

                public void setStatus(String status) {
                    this.status = status;
                }

                public String getStatus() {
                    return status;
                }

                public void setIsBlock(String isBlock) {
                    this.isBlock = isBlock;
                }

                public String getIsBlock() {
                    return isBlock;
                }

                public void setBlockStatus(String blockStatus) {
                    this.blockStatus = blockStatus;
                }

                public String getBlockStatus() {
                    return blockStatus;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getName() {
                    return name;
                }
            }
        }
    }
}