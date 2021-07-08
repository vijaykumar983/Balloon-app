package com.balloon.ui.components.activities.loginMain;

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
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.balloon.R;
import com.balloon.databinding.ActivityLoginMainBinding;
import com.balloon.databinding.DialogSuccessBinding;
import com.balloon.databinding.DialogSuccessfullyBinding;
import com.balloon.network.ApiResponse;
import com.balloon.pojo.LoginCheckData;
import com.balloon.ui.base.BaseBindingActivity;
import com.balloon.ui.components.activities.login.LoginActivity;
import com.balloon.ui.components.activities.verification.VerificationActivity;
import com.balloon.utils.Constants;
import com.balloon.utils.ProgressDialog;
import com.balloon.utils.SessionManager;
import com.balloon.utils.SingleShotLocationProvider;
import com.balloon.utils.Utility;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.util.HashMap;

import static com.balloon.utils.SingleShotLocationProvider.requestSingleUpdate;


public class LoginMainActivity extends BaseBindingActivity {
    private static final String TAG = LoginMainActivity.class.getName();
    private ActivityLoginMainBinding binding;
    private LoginMainViewModel viewModel = null;
    public static final int REQUEST_CHECK_SETTINGS = 123;


    @Override
    protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_main);
        viewModel = new ViewModelProvider(this).get(LoginMainViewModel.class);
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

        viewModel.responseLiveData.observe(this, new Observer<ApiResponse<LoginCheckData>>() {
            @Override
            public void onChanged(ApiResponse<LoginCheckData> it) {
                handleResult(it);
            }
        });
    }

    @Override
    protected void setListeners() {
        binding.btnLogin.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        Utility.hideKeyboard(mActivity);
        switch (view.getId()) {
            case R.id.btnLogin:
                loginCheckAPI();
                break;
        }
    }


    private void loginCheckAPI() {
        String phone = binding.etPhone.getText().toString().trim();
        if (viewModel.isValidFormData(mActivity, phone)) {

            HashMap<String, String> reqData = new HashMap<>();
            reqData.put("mobileNo", phone);

            if (Utility.isOnline(mActivity)) {
                Log.e(TAG, "Api parameters - " + reqData.toString());
                viewModel.loginCheck(reqData);
            } else {
                showNoInternetDialog();
            }
        }
    }

    private void handleResult(ApiResponse<LoginCheckData> result) {
        switch (result.getStatus()) {
            case ERROR:
                ProgressDialog.hideProgressDialog();
                Utility.showSnackBarMsgError(mActivity, result.getError().getMessage());
                Log.e(TAG, "error - " + result.getError().getMessage());
                break;
            case LOADING:
                ProgressDialog.showProgressDialog(mActivity);
                break;
            case SUCCESS:
                ProgressDialog.hideProgressDialog();
                Log.e(TAG, "Response - " + new Gson().toJson(result));
                if (result.getData().getStatusCode() == Constants.Success) {

                    if (result.getData().getUserStatus().equalsIgnoreCase("old")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("userId", result.getData().getUserId());
                        bundle.putString("otp", String.valueOf(result.getData().getOtp()));
                        bundle.putString("mobile", binding.etPhone.getText().toString().trim());
                        VerificationActivity.startActivity(mActivity, bundle, false);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("userType", "new");
                        bundle.putString("mobile", binding.etPhone.getText().toString().trim());
                        LoginActivity.startActivity(mActivity, bundle, false);
                    }
                    //HomeActivity.startActivity(mActivity, null, true);
                    //finish();

                } else {
                    Utility.showToastMessageError(mActivity, "something wrong");
                }
                break;
        }
    }

    private void showNoInternetDialog() {
        final Dialog dialog = new Dialog(mActivity, R.style.Theme_Dialog);
        DialogSuccessfullyBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.dialog_successfully, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialogBinding.tvTitle.setText(getResources().getString(R.string.internet_issue));
        dialogBinding.tvMessage.setText(getResources().getString(R.string.please_check_your_internet));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setContentView(dialogBinding.getRoot());
        dialogBinding.btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    public static void startActivity(Activity activity, Bundle bundle, boolean isClear) {
        Intent intent = new Intent(activity, LoginMainActivity.class);
        if (bundle != null) intent.putExtra("bundle", bundle);
        if (isClear)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    @Override
    public void onDestroy() {
        viewModel.disposeSubscriber();
        ProgressDialog.hideProgressDialog();
        super.onDestroy();
    }


}





