package com.balloon.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryData {

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

        @SerializedName("Category")
        private List<CategoryItem> category;

        @SerializedName("userId")
        private int userId;

        public void setCategory(List<CategoryItem> category) {
            this.category = category;
        }

        public List<CategoryItem> getCategory() {
            return category;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getUserId() {
            return userId;
        }


        public static class CategoryItem {

            @SerializedName("id")
            private String id;

            @SerializedName("title")
            private String title;

            public void setId(String id) {
                this.id = id;
            }

            public String getId() {
                return id;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getTitle() {
                return title;
            }
        }

    }

}