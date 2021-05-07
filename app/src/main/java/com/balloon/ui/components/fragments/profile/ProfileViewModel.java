package com.balloon.ui.components.fragments.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.balloon.network.ApiResponse;
import com.balloon.network.RestApi;
import com.balloon.network.RestApiFactory;
import com.balloon.pojo.EditProfileData;
import com.balloon.pojo.ProfileData;
import com.balloon.pojo.UploadImageData;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import retrofit2.http.Part;

public class ProfileViewModel extends ViewModel {
    MutableLiveData<ApiResponse<ProfileData>> responseLiveData = new MutableLiveData<>();
    ApiResponse<ProfileData> apiResponse = null;

    MutableLiveData<ApiResponse<UploadImageData>> responseLiveUploadImageData = new MutableLiveData<>();
    ApiResponse<UploadImageData> apiUploadImageResponse = null;


    private RestApi restApi = null;
    private Disposable subscription = null;


    {
        restApi = RestApiFactory.create();
        apiResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
        apiUploadImageResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
    }

    public final void getProfile(HashMap<String, String> reqData) {
        subscription = restApi.getProfile(reqData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        responseLiveData.postValue(apiResponse.loading());
                    }
                })
                .subscribe(new Consumer<ProfileData>() {
                    @Override
                    public void accept(ProfileData profileData) throws Exception {
                        responseLiveData.postValue(apiResponse.success(profileData));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        responseLiveData.postValue(apiResponse.error(throwable));
                    }
                });

    }

    public final void uploadImage(@Part MultipartBody.Part userId,
                                  @Part MultipartBody.Part image) {
        subscription = restApi.uploadImage(userId,image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        responseLiveUploadImageData.postValue(apiUploadImageResponse.loading());
                    }
                })
                .subscribe(new Consumer<UploadImageData>() {
                    @Override
                    public void accept(UploadImageData uploadImageData) throws Exception {
                        responseLiveUploadImageData.postValue(apiUploadImageResponse.success(uploadImageData));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        responseLiveUploadImageData.postValue(apiUploadImageResponse.error(throwable));
                    }
                });

    }


    public void disposeSubscriber() {
        if (subscription != null)
            subscription.dispose();
    }
}