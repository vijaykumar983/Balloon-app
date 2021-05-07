package com.balloon.ui.components.fragments.selectBalloon;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.balloon.network.ApiResponse;
import com.balloon.network.RestApi;
import com.balloon.network.RestApiFactory;
import com.balloon.pojo.BalloonListData;
import com.balloon.pojo.CategoryData;
import com.balloon.pojo.ProfileData;
import com.balloon.pojo.SendBalloonData;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SelectBalloonViewModel extends ViewModel {
    MutableLiveData<ApiResponse<SendBalloonData>> responseLiveData = new MutableLiveData<>();
    ApiResponse<SendBalloonData> apiResponse = null;

    MutableLiveData<ApiResponse<CategoryData>> responseLiveCategoryData = new MutableLiveData<>();
    ApiResponse<CategoryData> apiCategoryResponse = null;

    MutableLiveData<ApiResponse<BalloonListData>> responseLiveBalloonListData = new MutableLiveData<>();
    ApiResponse<BalloonListData> apiBalloonListResponse = null;


    private RestApi restApi = null;
    private Disposable subscription = null;


    {
        restApi = RestApiFactory.create();
        apiResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
        apiCategoryResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
        apiBalloonListResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
    }

    public final void sendBalloon(HashMap<String, String> reqData) {
        subscription = restApi.sendBalloon(reqData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        responseLiveData.postValue(apiResponse.loading());
                    }
                })
                .subscribe(new Consumer<SendBalloonData>() {
                    @Override
                    public void accept(SendBalloonData sendBalloonData) throws Exception {
                        responseLiveData.postValue(apiResponse.success(sendBalloonData));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        responseLiveData.postValue(apiResponse.error(throwable));
                    }
                });

    }

    public final void category(HashMap<String, String> reqData) {
        subscription = restApi.category(reqData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        responseLiveCategoryData.postValue(apiCategoryResponse.loading());
                    }
                })
                .subscribe(new Consumer<CategoryData>() {
                    @Override
                    public void accept(CategoryData categoryData) throws Exception {
                        responseLiveCategoryData.postValue(apiCategoryResponse.success(categoryData));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        responseLiveCategoryData.postValue(apiCategoryResponse.error(throwable));
                    }
                });

    }

    public final void balloonList(HashMap<String, String> reqData) {
        subscription = restApi.balloonList(reqData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        responseLiveBalloonListData.postValue(apiBalloonListResponse.loading());
                    }
                })
                .subscribe(new Consumer<BalloonListData>() {
                    @Override
                    public void accept(BalloonListData balloonListData) throws Exception {
                        responseLiveBalloonListData.postValue(apiBalloonListResponse.success(balloonListData));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        responseLiveBalloonListData.postValue(apiBalloonListResponse.error(throwable));
                    }
                });

    }




    public void disposeSubscriber() {
        if (subscription != null)
            subscription.dispose();
    }
}