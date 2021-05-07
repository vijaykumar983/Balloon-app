package com.balloon.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
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


		public static class BubblesItem implements Serializable {

			@SerializedName("id")
			private String id;

			@SerializedName("title")
			private String title;

			@SerializedName("Name")
			private String name;

			@SerializedName("image")
			private String image;

			public BubblesItem(String id,String title,String name,String image)
			{
				this.id = id;
				this.title = title;
				this.name = name;
				this.image = image;
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

			public void setImage(String image){
				this.image = image;
			}

			public String getImage(){
				return image;
			}
		}

	}

}