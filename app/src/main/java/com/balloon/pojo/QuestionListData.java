package com.balloon.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionListData{

	@SerializedName("data")
	private Data data;

	@SerializedName("message")
	private String message;

	@SerializedName("statusCode")
	private int statusCode;

	@SerializedName("ishow")
	private int ishow;

	@SerializedName("senderId")
	private int senderId;

	@SerializedName("senderName")
	private String senderName;

	@SerializedName("senderPhoto")
	private String senderPhoto;

	@SerializedName("requestId")
	private String requestId;

	public Data getData(){
		return data;
	}

	public String getMessage(){
		return message;
	}

	public int getIshow(){
		return ishow;
	}

	public int getSenderId(){
		return senderId;
	}

	public String getSenderName(){
		return senderName;
	}

	public String getSenderPhoto(){
		return senderPhoto;
	}

	public String getRequestId(){
		return requestId;
	}

	public int getStatusCode(){
		return statusCode;
	}


	public static class Data{

		@SerializedName("Category")
		private List<CategoryItem> category;

		@SerializedName("userId")
		private int userId;

		public List<CategoryItem> getCategory(){
			return category;
		}

		public int getUserId(){
			return userId;
		}

		public static class CategoryItem{

			@SerializedName("id")
			private String id;

			@SerializedName("title")
			private String title;

			public String getId(){
				return id;
			}

			public String getTitle(){
				return title;
			}
		}

	}
}