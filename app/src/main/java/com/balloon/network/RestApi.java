package com.balloon.network;

import com.balloon.pojo.BalloonListData;
import com.balloon.pojo.CategoryData;
import com.balloon.pojo.EditProfileData;
import com.balloon.pojo.LoginData;
import com.balloon.pojo.ProfileData;
import com.balloon.pojo.ResendOtpData;
import com.balloon.pojo.SendBalloonData;
import com.balloon.pojo.UploadImageData;
import com.balloon.pojo.VerifyOtpData;

import java.util.HashMap;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RestApi {
    @Multipart
    @POST("/balloon/Webservice/loginWithPhone")
    Observable<LoginData> login(@Part MultipartBody.Part name,
                                @Part MultipartBody.Part phone,
                                @Part MultipartBody.Part location,
                                @Part MultipartBody.Part deviceId,
                                @Part MultipartBody.Part profile_pic);

    @FormUrlEncoded
    @POST("/balloon/Webservice/otpverify")
    Observable<VerifyOtpData> verifyOtp(@FieldMap HashMap<String, String> reqData);

    @FormUrlEncoded
    @POST("/balloon/Webservice/resendotp")
    Observable<ResendOtpData> resendOtp(@FieldMap HashMap<String, String> reqData);

    @Multipart
    @POST("/balloon/Webservice/editProfile")
    Observable<EditProfileData> editProfile(@Part MultipartBody.Part userId,
                                            @Part MultipartBody.Part name,
                                            @Part MultipartBody.Part location,
                                            @Part MultipartBody.Part profile_pic,
                                            @Part MultipartBody.Part bio);

    @FormUrlEncoded
    @POST("/balloon/Webservice/profile")
    Observable<ProfileData> getProfile(@FieldMap HashMap<String, String> reqData);

    @FormUrlEncoded
    @POST("/balloon/Webservice/sendbubble")
    Observable<SendBalloonData> sendBalloon(@FieldMap HashMap<String, String> reqData);

    @FormUrlEncoded
    @POST("/balloon/Webservice/bubbleList")
    Observable<BalloonListData> balloonList(@FieldMap HashMap<String, String> reqData);

    @FormUrlEncoded
    @POST("/balloon/Webservice/category")
    Observable<CategoryData> category(@FieldMap HashMap<String, String> reqData);

    @Multipart
    @POST("/balloon/Webservice/uploadImages")
    Observable<UploadImageData> uploadImage(@Part MultipartBody.Part userId,
                                            @Part MultipartBody.Part image);
}
