package com.balloon.ui.components.activities.loginMain;

import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.balloon.R;
import com.balloon.network.ApiResponse;
import com.balloon.network.RestApi;
import com.balloon.network.RestApiFactory;
import com.balloon.pojo.LoginCheckData;
import com.balloon.utils.Utility;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LoginMainViewModel extends ViewModel {
    MutableLiveData<ApiResponse<LoginCheckData>> responseLiveData = new MutableLiveData<>();
    ApiResponse<LoginCheckData> apiResponse = null;


    private RestApi restApi = null;
    private Disposable subscription = null;


    {
        restApi = RestApiFactory.create();
        apiResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
    }

    public final void loginCheck(HashMap<String, String> reqData) {
        subscription = restApi.loginCheck(reqData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        responseLiveData.postValue(apiResponse.loading());
                    }
                })
                .subscribe(new Consumer<LoginCheckData>() {
                    @Override
                    public void accept(LoginCheckData loginCheckData) throws Exception {
                        responseLiveData.postValue(apiResponse.success(loginCheckData));
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

    /*Validations*/

    public boolean isValidFormData(AppCompatActivity mActivity, String phone) {

        if (TextUtils.isEmpty(phone)) {
            Utility.showSnackBarMsgError(mActivity, mActivity.getString(R.string.enter_mobile));
            return false;
        }
        if (phone.length() < 9) {
            Utility.showSnackBarMsgError(mActivity, mActivity.getString(R.string.enter_valid_mobile_number));
            return false;
        }

        return true;
    }
}
