package com.balloon.ui.components.fragments.chat;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.balloon.network.ApiResponse;
import com.balloon.network.RestApi;
import com.balloon.network.RestApiFactory;
import com.balloon.pojo.BlockUnblockData;
import com.balloon.pojo.QuestionListData;
import com.balloon.pojo.SubmitReviewData;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ChatViewModel extends ViewModel {

    MutableLiveData<ApiResponse<QuestionListData>> responseLiveQuestionListData = new MutableLiveData<>();
    ApiResponse<QuestionListData> apiQuestionListResponse = null;

    MutableLiveData<ApiResponse<SubmitReviewData>> responseLiveSubmitReviewData = new MutableLiveData<>();
    ApiResponse<SubmitReviewData> apiSubmitReviewResponse = null;

    MutableLiveData<ApiResponse<BlockUnblockData>> responseLiveBlockUnblockData = new MutableLiveData<>();
    ApiResponse<BlockUnblockData> apiBlockUnblockResponse = null;


    private RestApi restApi = null;
    private Disposable subscription = null;


    {
        restApi = RestApiFactory.create();
        apiQuestionListResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
        apiSubmitReviewResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
        apiBlockUnblockResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
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

    public final void blockDeleteApi(HashMap<String, String> reqData) {
        subscription = restApi.blockUnblock(reqData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        responseLiveBlockUnblockData.postValue(apiBlockUnblockResponse.loading());
                    }
                })
                .subscribe(new Consumer<BlockUnblockData>() {
                    @Override
                    public void accept(BlockUnblockData blockUnblockData) throws Exception {
                        responseLiveBlockUnblockData.postValue(apiBlockUnblockResponse.success(blockUnblockData));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        responseLiveBlockUnblockData.postValue(apiBlockUnblockResponse.error(throwable));
                    }
                });

    }


    public void disposeSubscriber() {
        if (subscription != null)
            subscription.dispose();
    }
}