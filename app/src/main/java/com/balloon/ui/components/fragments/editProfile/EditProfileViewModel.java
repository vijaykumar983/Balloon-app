package com.balloon.ui.components.fragments.editProfile;

import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.balloon.R;
import com.balloon.network.ApiResponse;
import com.balloon.network.RestApi;
import com.balloon.network.RestApiFactory;
import com.balloon.pojo.EditProfileData;
import com.balloon.pojo.LoginData;
import com.balloon.pojo.ProfileData;
import com.balloon.utils.SessionManager;
import com.balloon.utils.Utility;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import retrofit2.http.Part;

public class EditProfileViewModel extends ViewModel {
    MutableLiveData<ApiResponse<EditProfileData>> responseLiveData = new MutableLiveData<>();
    ApiResponse<EditProfileData> apiResponse = null;

    MutableLiveData<ApiResponse<ProfileData>> responseLiveProfileData = new MutableLiveData<>();
    ApiResponse<ProfileData> apiProfileResponse = null;


    private RestApi restApi = null;
    private Disposable subscription = null;


    {
        restApi = RestApiFactory.create();
        apiResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
        apiProfileResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
    }

    public final void editProfile(@Part MultipartBody.Part userId,
                                  @Part MultipartBody.Part name,
                            @Part MultipartBody.Part location,
                            @Part MultipartBody.Part profile_pic,
                                  @Part MultipartBody.Part bio,
                                  @Part MultipartBody.Part phone) {
        subscription = restApi.editProfile(userId,name, location, profile_pic,bio,phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        responseLiveData.postValue(apiResponse.loading());
                    }
                })
                .subscribe(new Consumer<EditProfileData>() {
                    @Override
                    public void accept(EditProfileData editProfileData) throws Exception {
                        responseLiveData.postValue(apiResponse.success(editProfileData));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        responseLiveData.postValue(apiResponse.error(throwable));
                    }
                });

    }

    public final void getProfile(HashMap<String, String> reqData) {
        subscription = restApi.getProfile(reqData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        responseLiveProfileData.postValue(apiProfileResponse.loading());
                    }
                })
                .subscribe(new Consumer<ProfileData>() {
                    @Override
                    public void accept(ProfileData profileData) throws Exception {
                        responseLiveProfileData.postValue(apiProfileResponse.success(profileData));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        responseLiveProfileData.postValue(apiProfileResponse.error(throwable));
                    }
                });

    }


    public void disposeSubscriber() {
        if (subscription != null)
            subscription.dispose();
    }


    /*Validations */
    public boolean isValidFormData(AppCompatActivity mActivity, String image, String name, String location,String bio,String phone) {

        if (!TextUtils.isEmpty(image) && image.equals("Upload Image")) {
            Utility.showToastMessageError(mActivity, mActivity.getString(R.string.please_select_image));
            return false;
        }
        if (TextUtils.isEmpty(name)) {
            Utility.showToastMessageError(mActivity, mActivity.getString(R.string.enter_name));
            return false;
        }

        if (name.length() < 3) {
            Utility.showToastMessageError(mActivity, mActivity.getString(R.string.minimum_3_char_long_name));
            return false;
        }

        if (SessionManager.getInstance(mActivity).getSocial()) {
            if (TextUtils.isEmpty(phone)) {
                Utility.showSnackBarMsgError(mActivity, mActivity.getString(R.string.enter_mobile));
                return false;
            }
            if (phone.length() < 9) {
                Utility.showSnackBarMsgError(mActivity, mActivity.getString(R.string.enter_valid_mobile_number));
                return false;
            }
        }

        if (TextUtils.isEmpty(location)) {
            Utility.showToastMessageError(mActivity, mActivity.getString(R.string.enter_address));
            return false;
        }

        if (location.length() < 3) {
            Utility.showToastMessageError(mActivity, mActivity.getString(R.string.minimum_3_char_long_address));
            return false;
        }
        if (TextUtils.isEmpty(bio)) {
            Utility.showToastMessageError(mActivity, mActivity.getString(R.string.enter_bio));
            return false;
        }

        if (bio.length() < 3) {
            Utility.showToastMessageError(mActivity, mActivity.getString(R.string.minimum_3_char_long_bio));
            return false;
        }

        return true;
    }
}