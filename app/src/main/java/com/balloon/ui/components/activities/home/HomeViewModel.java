package com.balloon.ui.components.activities.home;

import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    // MutableLiveData<ApiResponse<SignupData>> responseLiveData = new MutableLiveData<ApiResponse<SignupData>>();
    //  ApiResponse<SignupData> apiResponse = null;
    // private RestApi restApi = null;
    //private Disposable subscription = null;

    {
        //    restApi = RestApiFactory.create()
        //    apiResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
    }

    /* public void signup(HashMap<String, String> reqData ) {

         subscription = restApi.signup(reqData)
             .subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .doOnSubscribe {
                 responseLiveData.postValue(apiResponse.loading())
             }
             .subscribe(
                 { result ->
                     responseLiveData.postValue(apiResponse.success(result))
                 },
                 { throwable ->
                     responseLiveData.postValue(apiResponse.error(throwable))
                 }
             )
     }*/

      /*public void disposeSubscriber() {
          if (subscription != null)
              subscription.dispose();
      }*/
}
