package com.balloon.ui.components.activities.loginOption;

import androidx.lifecycle.ViewModel;

public class LoginOptionViewModel extends ViewModel {
  /*  MutableLiveData<ApiResponse<LoginSignupData>> responseLiveData = new MutableLiveData<>();
    ApiResponse<LoginSignupData> apiResponse = null;


    private RestApi restApi = null;
    private Disposable subscription = null;


    {
        restApi = RestApiFactory.create();
        apiResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
    }

    public final void login_signup(HashMap<String, String> reqData) {
        subscription = restApi.loginSignup(reqData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        responseLiveData.postValue(apiResponse.loading());
                    }
                })
                .subscribe(new Consumer<LoginSignupData>() {
                    @Override
                    public void accept(LoginSignupData loginSignupData) throws Exception {
                        responseLiveData.postValue(apiResponse.success(loginSignupData));
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


    *//*Validations*//*
    public boolean isValidFormData(AppCompatActivity mActivity, String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            Utility.showSnackBarMsgError(mActivity, mActivity.getString(R.string.enter_valid_mobile_number));
            return false;
        }
        if (mobile.length() < 9) {
            Utility.showSnackBarMsgError(mActivity, mActivity.getString(R.string.enter_valid_mobile_number));
            return false;
        }
        return true;
    }*/

}
