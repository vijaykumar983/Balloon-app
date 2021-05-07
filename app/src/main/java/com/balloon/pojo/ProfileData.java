package com.balloon.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfileData{

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

		@SerializedName("userId")
		private String userId;

		@SerializedName("userinfo")
		private Userinfo userinfo;

		public void setUserId(String userId){
			this.userId = userId;
		}

		public String getUserId(){
			return userId;
		}

		public void setUserinfo(Userinfo userinfo){
			this.userinfo = userinfo;
		}

		public Userinfo getUserinfo(){
			return userinfo;
		}


		public static class Userinfo{

			@SerializedName("image")
			private String image;

			@SerializedName("images")
			private List<ImagesItem> images;

			@SerializedName("phone")
			private String phone;

			@SerializedName("name")
			private String name;

			@SerializedName("bio")
			private String bio;

			@SerializedName("location")
			private String location;

			@SerializedName("id")
			private String id;

			@SerializedName("base_urlImage")
			private String baseUrlImage;

			public void setImage(String image){
				this.image = image;
			}

			public String getImage(){
				return image;
			}

			public void setImages(List<ImagesItem> images){
				this.images = images;
			}

			public List<ImagesItem> getImages(){
				return images;
			}

			public void setPhone(String phone){
				this.phone = phone;
			}

			public String getPhone(){
				return phone;
			}

			public void setName(String name){
				this.name = name;
			}

			public String getName(){
				return name;
			}

			public void setBio(String bio){
				this.bio = bio;
			}

			public String getBio(){
				return bio;
			}

			public void setLocation(String location){
				this.location = location;
			}

			public String getLocation(){
				return location;
			}

			public void setId(String id){
				this.id = id;
			}

			public String getId(){
				return id;
			}

			public void setBaseUrlImage(String baseUrlImage){
				this.baseUrlImage = baseUrlImage;
			}

			public String getBaseUrlImage(){
				return baseUrlImage;
			}


			public static class ImagesItem{

				@SerializedName("image")
				private String image;

				@SerializedName("created_at")
				private String createdAt;

				@SerializedName("id")
				private String id;

				@SerializedName("userId")
				private String userId;

				public void setImage(String image){
					this.image = image;
				}

				public String getImage(){
					return image;
				}

				public void setCreatedAt(String createdAt){
					this.createdAt = createdAt;
				}

				public String getCreatedAt(){
					return createdAt;
				}

				public void setId(String id){
					this.id = id;
				}

				public String getId(){
					return id;
				}

				public void setUserId(String userId){
					this.userId = userId;
				}

				public String getUserId(){
					return userId;
				}
			}
			
		}
		
	}
	
}