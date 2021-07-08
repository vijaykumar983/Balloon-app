package com.balloon.ui.components.fragments.userList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.balloon.network.ApiResponse;
import com.balloon.network.RestApi;
import com.balloon.network.RestApiFactory;
import com.balloon.pojo.AcceptRejectData;
import com.balloon.pojo.ChatUserListData;
import com.balloon.pojo.LoginSocialData;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class UserListViewModel extends ViewModel {
    MutableLiveData<ApiResponse<ChatUserListData>> responseLiveData = new MutableLiveData<>();
    ApiResponse<ChatUserListData> apiResponse = null;

    MutableLiveData<ApiResponse<AcceptRejectData>> responseLiveAcceptRejData = new MutableLiveData<>();
    ApiResponse<AcceptRejectData> apiAcceptRejResponse = null;


    private RestApi restApi = null;
    private Disposable subscription = null;


    {
        restApi = RestApiFactory.create();
        apiResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
        apiAcceptRejResponse = new ApiResponse<>(ApiResponse.Status.LOADING, null, null);
    }

    public final void chatUserListApi(HashMap<String, String> reqData) {
        subscription = restApi.chatUserList(reqData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        responseLiveData.postValue(apiResponse.loading());
                    }
                })
                .subscribe(new Consumer<ChatUserListData>() {
                    @Override
                    public void accept(ChatUserListData chatUserListData) throws Exception {
                        responseLiveData.postValue(apiResponse.success(chatUserListData));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        responseLiveData.postValue(apiResponse.error(throwable));
                    }
                });

    }

    public final void acceptRejectApi(HashMap<String, String> reqData) {
        subscription = restApi.acceptReject(reqData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        responseLiveAcceptRejData.postValue(apiAcceptRejResponse.loading());
                    }
                })
                .subscribe(new Consumer<AcceptRejectData>() {
                    @Override
                    public void accept(AcceptRejectData acceptRejectData) throws Exception {
                        responseLiveAcceptRejData.postValue(apiAcceptRejResponse.success(acceptRejectData));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        responseLiveAcceptRejData.postValue(apiAcceptRejResponse.error(throwable));
                    }
                });

    }


    public void disposeSubscriber() {
        if (subscription != null)
            subscription.dispose();
    }
}
