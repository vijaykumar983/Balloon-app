package com.balloon.ui.components.activities.login;

import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.balloon.R;
import com.balloon.network.ApiResponse;
import com.balloon.network.RestApi;
import com.balloon.network.RestApiFactory;
import com.balloon.pojo.LoginData;
import com.balloon.utils.Utility;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import retrofit2.http.Part;

public class LoginViewModel extends ViewModel {
    MutableLiveData<ApiResponse<LoginData>> responseLiveData = new MutableLiveData<>();
    ApiResponse<LoginData> apiResponse = null;


    private RestApi restApi = null;
    private Disposable subscription = null;


    {
        restApi = RestApiFactory.create();
        apiResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
    }

    public final void login(@Part MultipartBody.Part name,
                                    @Part MultipartBody.Part phone,
                                    @Part MultipartBody.Part location,
                                    @Part MultipartBody.Part deviceId,
                                    @Part MultipartBody.Part profile_pic) {
        subscription = restApi.login(name, phone, location,deviceId, profile_pic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        responseLiveData.postValue(apiResponse.loading());
                    }
                })
                .subscribe(new Consumer<LoginData>() {
                    @Override
                    public void accept(LoginData loginData) throws Exception {
                        responseLiveData.postValue(apiResponse.success(loginData));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        responseLiveData.postValue(apiResponse.error(throwable));
                    }
                });

    }


    public void disposeSubscriber() {
        if (subscription != null)
            subscription.dispose();
    }


    /*Validations */
    public boolean isValidFormData(AppCompatActivity mActivity, String image, String name, String phone, String location) {
        if (TextUtils.isEmpty(image)) {
            Utility.showSnackBarMsgError(mActivity, mActivity.getString(R.string.please_select_image));
            return false;
        }
        if (TextUtils.isEmpty(name)) {
            Utility.showSnackBarMsgError(mActivity, mActivity.getString(R.string.enter_name));
            return false;
        }

        if (name.length() < 3) {
            Utility.showSnackBarMsgError(mActivity, mActivity.getString(R.string.minimum_3_char_long_name));
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            Utility.showSnackBarMsgError(mActivity, mActivity.getString(R.string.enter_mobile));
            return false;
        }
        if (phone.length() < 9) {
            Utility.showSnackBarMsgError(mActivity, mActivity.getString(R.string.enter_valid_mobile_number));
            return false;
        }

        if (TextUtils.isEmpty(location)) {
            Utility.showSnackBarMsgError(mActivity, mActivity.getString(R.string.enter_address));
            return false;
        }

        if (location.length() < 3) {
            Utility.showSnackBarMsgError(mActivity, mActivity.getString(R.string.minimum_3_char_long_address));
            return false;
        }

        return true;
    }

}
