package com.balloon.pojo;

import com.google.gson.annotations.SerializedName;

public class LoginCheckData{

	@SerializedName("userStatus")
	private String userStatus;

	@SerializedName("otp")
	private String otp;

	@SerializedName("mobileNO")
	private String mobileNO;

	@SerializedName("userId")
	private String userId;

	@SerializedName("statusCode")
	private int statusCode;

	public void setUserStatus(String userStatus){
		this.userStatus = userStatus;
	}

	public String getUserStatus(){
		return userStatus;
	}

	public void setOtp(String otp){
		this.otp = otp;
	}

	public String getOtp(){
		return otp;
	}

	public void setMobileNO(String mobileNO){
		this.mobileNO = mobileNO;
	}

	public String getMobileNO(){
		return mobileNO;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return userId;
	}

	public void setStatusCode(int statusCode){
		this.statusCode = statusCode;
	}

	public int getStatusCode(){
		return statusCode;
	}
}