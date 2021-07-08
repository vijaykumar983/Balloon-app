package com.balloon.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BalloonListData{

	@SerializedName("data")
	private Data data;

	@SerializedName("message")
	private String message;

	@SerializedName("statusCode")
	private int statusCode;

	public void setData(Data data){
		this.data = data;
	}

	public Data getData(){
		return data;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setStatusCode(int statusCode){
		this.statusCode = statusCode;
	}

	public int getStatusCode(){
		return statusCode;
	}

	public static class Data{

		@SerializedName("Bubbles")
		private List<BubblesItem> bubbles;

		@SerializedName("userId")
		private String userId;

		public void setBubbles(List<BubblesItem> bubbles){
			this.bubbles = bubbles;
		}

		public List<BubblesItem> getBubbles(){
			return bubbles;
		}

		public void setUserId(String userId){
			this.userId = userId;
		}

		public String getUserId(){
			return userId;
		}

		public static class BubblesItem{

			@SerializedName("image")
			private String image;

			@SerializedName("categoryimage")
			private String categoryimage;

			@SerializedName("bubbleId")
			private String bubbleId;

			@SerializedName("id")
			private String id;

			@SerializedName("title")
			private String title;

			@SerializedName("Name")
			private String name;

			public void setImage(String image){
				this.image = image;
			}

			public String getImage(){
				return image;
			}

			public void setCategoryimage(String categoryimage){
				this.categoryimage = categoryimage;
			}

			public String getCategoryimage(){
				return categoryimage;
			}

			public void setBubbleId(String bubbleId){
				this.bubbleId = bubbleId;
			}

			public String getBubbleId(){
				return bubbleId;
			}

			public void setId(String id){
				this.id = id;
			}

			public String getId(){
				return id;
			}

			public void setTitle(String title){
				this.title = title;
			}

			public String getTitle(){
				return title;
			}

			public void setName(String name){
				this.name = name;
			}

			public String getName(){
				return name;
			}
		}
	}
}