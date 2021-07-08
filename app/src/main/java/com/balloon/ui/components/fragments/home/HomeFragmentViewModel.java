package com.balloon.ui.components.fragments.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.balloon.network.ApiResponse;
import com.balloon.network.RestApi;
import com.balloon.network.RestApiFactory;
import com.balloon.pojo.BalloonListData;
import com.balloon.pojo.QuestionListData;
import com.balloon.pojo.SendBalloonData;
import com.balloon.pojo.SendRequestData;
import com.balloon.pojo.SubmitReviewData;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HomeFragmentViewModel extends ViewModel {
    MutableLiveData<ApiResponse<BalloonListData>> responseLiveData = new MutableLiveData<>();
    ApiResponse<BalloonListData> apiResponse = null;

    MutableLiveData<ApiResponse<SendRequestData>> responseLiveSendReqData = new MutableLiveData<>();
    ApiResponse<SendRequestData> apiSendReqResponse = null;

    MutableLiveData<ApiResponse<QuestionListData>> responseLiveQuestionListData = new MutableLiveData<>();
    ApiResponse<QuestionListData> apiQuestionListResponse = null;

    MutableLiveData<ApiResponse<SubmitReviewData>> responseLiveSubmitReviewData = new MutableLiveData<>();
    ApiResponse<SubmitReviewData> apiSubmitReviewResponse = null;


    private RestApi restApi = null;
    private Disposable subscription = null;


    {
        restApi = RestApiFactory.create();
        apiResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
        apiSendReqResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
        apiQuestionListResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
        apiSubmitReviewResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
    }

    public final void balloonList(HashMap<String, String> reqData) {
        subscription = restApi.balloonList(reqData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        responseLiveData.postValue(apiResponse.loading());
                    }
                })
                .subscribe(new Consumer<BalloonListData>() {
                    @Override
                    public void accept(BalloonListData balloonListData) throws Exception {
                        responseLiveData.postValue(apiResponse.success(balloonListData));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        responseLiveData.postValue(apiResponse.error(throwable));
                    }
                });

    }

    public final void sendRequestApi(HashMap<String, String> reqData) {
        subscription = restApi.sendRequest(reqData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        responseLiveSendReqData.postValue(apiSendReqResponse.loading());
                    }
                })
                .subscribe(new Consumer<SendRequestData>() {
                    @Override
                    public void accept(SendRequestData sendBalloonData) throws Exception {
                        responseLiveSendReqData.postValue(apiSendReqResponse.success(sendBalloonData));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        responseLiveSendReqData.postValue(apiSendReqResponse.error(throwable));
                    }
                });

    }

    public final void questionListApi(HashMap<String, String> reqData) {
        subscription = restApi.questionList(reqData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        responseLiveQuestionListData.postValue(apiQuestionListResponse.loading());
                    }
                })
                .subscribe(new Consumer<QuestionListData>() {
                    @Override
                    public void accept(QuestionListData questionListData) throws Exception {
                        responseLiveQuestionListData.postValue(apiQuestionListResponse.success(questionListData));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        responseLiveQuestionListData.postValue(apiQuestionListResponse.error(throwable));
                    }
                });

    }
    public final void submitReviewApi(HashMap<String, String> reqData) {
        subscription = restApi.submitReview(reqData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        responseLiveSubmitReviewData.postValue(apiSubmitReviewResponse.loading());
                    }
                })
                .subscribe(new Consumer<SubmitReviewData>() {
                    @Override
                    public void accept(SubmitReviewData submitReviewData) throws Exception {
                        responseLiveSubmitReviewData.postValue(apiSubmitReviewResponse.success(submitReviewData));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        responseLiveSubmitReviewData.postValue(apiSubmitReviewResponse.error(throwable));
                    }
                });

    }


    public void disposeSubscriber() {
        if (subscription != null)
            subscription.dispose();
    }
}