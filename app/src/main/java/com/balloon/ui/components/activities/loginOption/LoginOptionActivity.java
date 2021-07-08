package com.balloon.ui.components.activities.loginOption;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.balloon.R;
import com.balloon.databinding.ActivityLoginOptionBinding;
import com.balloon.databinding.DialogSuccessBinding;
import com.balloon.network.ApiResponse;
import com.balloon.pojo.LoginSocialData;
import com.balloon.ui.base.BaseBindingActivity;
import com.balloon.ui.components.activities.home.HomeActivity;
import com.balloon.ui.components.activities.login.LoginActivity;
import com.balloon.ui.components.activities.loginMain.LoginMainActivity;
import com.balloon.utils.Constants;
import com.balloon.utils.ProgressDialog;
import com.balloon.utils.SessionManager;
import com.balloon.utils.SingleShotLocationProvider;
import com.balloon.utils.Utility;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.balloon.utils.SingleShotLocationProvider.requestSingleUpdate;


public class LoginOptionActivity extends BaseBindingActivity {
    private static final String TAG = LoginOptionActivity.class.getName();
    private ActivityLoginOptionBinding binding;
    private LoginOptionViewModel viewModel = null;
    private CallbackManager callbackManager;
    private int RC_SIGN_IN = 128;
    private GoogleSignInClient mGoogleSignInClient;
    public static final int REQUEST_CHECK_SETTINGS = 123;


    @Override
    protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_option);
        viewModel = new ViewModelProvider(this).get(LoginOptionViewModel.class);
        binding.setLifecycleOwner(this);
    }

    @Override
    protected void createActivityObject(@Nullable Bundle savedInstanceState) {
        mActivity = this;
    }

    @SuppressLint("ResourceType")
    @Override
    protected void initializeObject() {
        checkWhetherLocationSettingsAreSatisfied();
        callbackManager = CallbackManager.Factory.create();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getResources().getString(R.string.googleAccountWebClientID))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        viewModel.responseLiveData.observe(this, new Observer<ApiResponse<LoginSocialData>>() {
            @Override
            public void onChanged(ApiResponse<LoginSocialData> it) {
                handleResult(it);
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        if (task.getResult() != null) {
                            String token = task.getResult().getToken();
                            sessionManager.setDEVICE_TOKEN(token);
                            Log.w(TAG, "token -" + token);
                        }
                    }
                });
    }

    @Override
    protected void setListeners() {
        binding.tvLoginPhone.setOnClickListener(this);
        binding.llLoginFacebook.setOnClickListener(this);
        binding.tvLoginGoogle.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        Utility.hideKeyboard(mActivity);
        switch (view.getId()) {
            case R.id.tvLoginPhone:
                //LoginActivity.startActivity(mActivity, null, false);
                LoginMainActivity.startActivity(mActivity, null, false);
                break;
            case R.id.llLoginFacebook:
                facebookLogin();
                break;
            case R.id.tvLoginGoogle:
                googleSignIn();
                break;

        }
    }

    private void checkWhetherLocationSettingsAreSatisfied() {

        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setNumUpdates(2);

        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        builder.setNeedBle(true);
        SettingsClient client = LocationServices.getSettingsClient(mActivity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(mActivity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.w(TAG, "onSuccess() called with: locationSettingsResponse = [" + locationSettingsResponse + "]");
                //hasLocationPermission();


                try {
                    requestSingleUpdate(mActivity, new SingleShotLocationProvider.LocationCallback() {
                        @Override
                        public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                            Log.e(TAG, "my location is - " + location.latitude + " " + location.longitude);
                            if (!SessionManager.getInstance(mActivity).isSELECT_LOCATION()) {
                                sessionManager.setLOCATION(Utility.getCompleteAddressString(mActivity, location.latitude, location.longitude));
                                sessionManager.setLATITUDE(String.valueOf(location.latitude));
                                sessionManager.setLONGITUDE(String.valueOf(location.longitude));
                                //binding.etLocation.setText(sessionManager.getLOCATION());
                                //binding.etLocation.setSelected(true);
                            }
                            Log.e(TAG, "location - " + sessionManager.getLOCATION());
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, "error - " + e.getMessage());
                }

            }
        });
        task.addOnFailureListener(mActivity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception error) {
                Log.d(TAG, "onSuccess --> onFailure() called with: e = [" + error + "]");
                if (error instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) error;
                        resolvable.startResolutionForResult(mActivity, REQUEST_CHECK_SETTINGS);

                    } catch (IntentSender.SendIntentException e) {

                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void buildAlertMessageNoGps() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        DialogSuccessBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.dialog_success, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.tvTitle.setText("GPS Settings");
        dialogBinding.tvMessage.setText("GPS is not enabled. Please goto settings page to enable");
        dialogBinding.tvOk.setText("Settings");
        dialogBinding.llOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                finish();
            }
        });

        dialog.show();
    }


    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void facebookLogin() {
        if (!isLoggedIn()) {
            LoginManager.getInstance().logOut();
        }//binding.loginButton.setReadPermissions("email", "public_profile", "user_friends");
        binding.loginButton.setReadPermissions("email", "public_profile");
        binding.loginButton.performClick();
        //  binding.loginButton.setFragment(SignInActivity.this);
        binding.loginButton.setPressed(true);
        binding.loginButton.invalidate();

        binding.loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.e(TAG, "response: " + response + "");
                                try {
                                    if (object != null) {
                                        Map<String, String> map = new HashMap<>();
                                        map.put("socialId", object.getString("id"));
                                        map.put("fullName", object.getString("name"));
                                        if (object.has("email"))
                                            map.put("email", object.getString("email"));
                                        else
                                            map.put("email", "");
                                        map.put("socialType", Constants.SOCIAL_TYPE_FACEBOOK);
                                        map.put("picture", object.getJSONObject("picture").getJSONObject("data").getString("url"));

                                        // LoginManager.getInstance().logOut();
                                        Log.e(TAG, "facebook response - " + map.toString());
                                        socialSignApi(map, "facebook");

                                        try {
                                            LoginManager.getInstance().logOut();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "cancel");
            }

            @Override
            public void onError(FacebookException e) {
                Log.e(TAG, "error : " + e.getMessage());
            }
        });

        binding.loginButton.setPressed(false);
        binding.loginButton.invalidate();

    }

    private boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && !accessToken.isExpired();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                try {
                    requestSingleUpdate(mActivity, new SingleShotLocationProvider.LocationCallback() {
                        @Override
                        public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                            Log.e(TAG, "my location is - " + location.latitude + " " + location.longitude);
                            if (!SessionManager.getInstance(mActivity).isSELECT_LOCATION()) {
                                sessionManager.setLOCATION(Utility.getCompleteAddressString(mActivity, location.latitude, location.longitude));
                                sessionManager.setLATITUDE(String.valueOf(location.latitude));
                                sessionManager.setLONGITUDE(String.valueOf(location.longitude));
                                //binding.etLocation.setText(sessionManager.getLOCATION());
                                //binding.etLocation.setSelected(true);
                            }
                            Log.e(TAG, "location - " + sessionManager.getLOCATION());
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, "error - " + e.getMessage());
                }

            } else {
                //User clicks No
                buildAlertMessageNoGps();
            }
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Map<String, String> map = new HashMap<>();
            map.put("socialId", account.getId());
            map.put("fullName", account.getDisplayName());
            map.put("email", account.getEmail());
            map.put("socialType", Constants.SOCIAL_TYPE_GOOGLE);
            map.put("picture", String.valueOf(account.getPhotoUrl()));

            Log.e(TAG, "google response - " + map.toString());
            socialSignApi(map, "google");

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void socialSignApi(Map<String, String> map, String type) {
        if (map != null) {

            HashMap<String, String> reqData = new HashMap<>();
            reqData.put("appid", map.get("socialId"));
            reqData.put("email", map.get("email"));
            reqData.put("name", map.get("fullName"));
            reqData.put("mobileNo", "");
            reqData.put("profile_image", map.get("picture"));
            reqData.put("location", sessionManager.getADDRESS());
            reqData.put("lat", sessionManager.getLATITUDE());
            reqData.put("lng", sessionManager.getLONGITUDE());
            reqData.put("deviceId", sessionManager.getDEVICE_TOKEN());
            reqData.put("type", type);

            Log.e(TAG, "Api parameters - " + reqData.toString());
            viewModel.login_signup(reqData);
        }
    }

    private void handleResult(ApiResponse<LoginSocialData> result) {
        switch (result.getStatus()) {
            case ERROR:
                ProgressDialog.hideProgressDialog();
                Utility.showToastMessageError(mActivity, result.getError().getMessage());
                Log.e(TAG, "error - " + result.getError().getMessage());
                break;
            case LOADING:
                ProgressDialog.showProgressDialog(this);
                break;
            case SUCCESS:
                ProgressDialog.hideProgressDialog();
                Log.e(TAG, "Response - " + new Gson().toJson(result));
                if (result.getData().getStatusCode() == Constants.Success) {

                    sessionManager.setFIREBASE_ID(result.getData().getData().getUserData().getFirebaseId());
                    sessionManager.setLogin();
                    if (result.getData().getIsSend() == 1) {
                        sessionManager.setSelectBalloon(false); //select balloon screen
                    } else {
                        sessionManager.setSelectBalloon(true); //select balloon screen
                    }

                    sessionManager.setSocial(true);
                    sessionManager.setUSER_ID(result.getData().getData().getUserId());
                    sessionManager.setFULL_NAME(result.getData().getData().getUserData().getName());
                    sessionManager.setPHONE(result.getData().getData().getUserData().getPhone());
                    sessionManager.setADDRESS(result.getData().getData().getUserData().getLocation());
                    sessionManager.setPROFILE_IMAGE(result.getData().getData().getUserData().getProfileImage());
                    HomeActivity.startActivity(mActivity, null, true);
                    finish();

                } else {
                    Utility.showToastMessageError(mActivity, result.getData().getMessage());
                }
                break;
        }
    }


    public static void startActivity(Activity activity, Bundle bundle, boolean isClear) {
        Intent intent = new Intent(activity, LoginOptionActivity.class);
        if (bundle != null) intent.putExtra("bundle", bundle);
        if (isClear)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    @Override
    public void onDestroy() {
        viewModel.disposeSubscriber();
        super.onDestroy();
    }
}
