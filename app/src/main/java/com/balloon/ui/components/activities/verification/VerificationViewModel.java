package com.balloon.ui.components.activities.verification;

import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.balloon.R;
import com.balloon.network.ApiResponse;
import com.balloon.network.RestApi;
import com.balloon.network.RestApiFactory;
import com.balloon.pojo.ResendOtpData;
import com.balloon.pojo.VerifyOtpData;
import com.balloon.utils.Utility;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class VerificationViewModel extends ViewModel {
    MutableLiveData<ApiResponse<VerifyOtpData>> responseLiveData = new MutableLiveData<>();
    ApiResponse<VerifyOtpData> apiResponse = null;

    MutableLiveData<ApiResponse<ResendOtpData>> responseLiveResendOtpData = new MutableLiveData<>();
    ApiResponse<ResendOtpData> apiResendOtpResponse = null;


    private RestApi restApi = null;
    private Disposable subscription = null;


    {
        restApi = RestApiFactory.create();
        apiResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
        apiResendOtpResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
    }

    public final void verifyOtp(HashMap<String, String> reqData) {
        subscription = restApi.verifyOtp(reqData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        responseLiveData.postValue(apiResponse.loading());
                    }
                })
                .subscribe(new Consumer<VerifyOtpData>() {
                    @Override
                    public void accept(VerifyOtpData verifyOtpData) throws Exception {
                        responseLiveData.postValue(apiResponse.success(verifyOtpData));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        responseLiveData.postValue(apiResponse.error(throwable));
                    }
                });

    }
    public final void resendOtp(HashMap<String, String> reqData) {
        subscription = restApi.resendOtp(reqData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        responseLiveResendOtpData.postValue(apiResendOtpResponse.loading());
                    }
                })
                .subscribe(new Consumer<ResendOtpData>() {
                    @Override
                    public void accept(ResendOtpData resendOtpData) throws Exception {
                        responseLiveResendOtpData.postValue(apiResendOtpResponse.success(resendOtpData));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        responseLiveResendOtpData.postValue(apiResendOtpResponse.error(throwable));
                    }
                });

    }

    public void disposeSubscriber() {
        if (subscription != null)
            subscription.dispose();
    }

    /*Validations*/
    public boolean isValidFormData(AppCompatActivity mActivity, String otp) {

        if (TextUtils.isEmpty(otp)) {
            Utility.showToastMessageError(mActivity, mActivity.getString(R.string.enter_otp));
            return false;
        }

        if (otp.length() < 4) {
            Utility.showToastMessageError(mActivity, mActivity.getString(R.string.enter_valid_otp));
            return false;
        }

        return true;
    }
}
