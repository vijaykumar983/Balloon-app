package com.balloon.ui.components.activities.verification;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import com.balloon.databinding.ActivityVerificationBinding;
import com.balloon.databinding.DialogSuccessfullyBinding;
import com.balloon.network.ApiResponse;
import com.balloon.pojo.ResendOtpData;
import com.balloon.pojo.VerifyOtpData;
import com.balloon.ui.base.BaseBindingActivity;
import com.balloon.ui.components.activities.home.HomeActivity;
import com.balloon.utils.Constants;
import com.balloon.utils.ProgressDialog;
import com.balloon.utils.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import java.util.HashMap;


public class VerificationActivity extends BaseBindingActivity {
    private static final String TAG = VerificationActivity.class.getName();
    private ActivityVerificationBinding binding;
    private VerificationViewModel viewModel = null;
    private Bundle mBundle;
    private String userId = "", mobile = "";


    @Override
    protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verification);
        viewModel = new ViewModelProvider(this).get(VerificationViewModel.class);
        binding.setLifecycleOwner(this);
    }

    @Override
    protected void createActivityObject(@Nullable Bundle savedInstanceState) {
        mActivity = this;
    }

    @SuppressLint("ResourceType")
    @Override
    protected void initializeObject() {
        getIntentData();
        viewModel.responseLiveData.observe(this, new Observer<ApiResponse<VerifyOtpData>>() {
            @Override
            public void onChanged(ApiResponse<VerifyOtpData> it) {
                handleResult(it);
            }
        });
        viewModel.responseLiveResendOtpData.observe(this, new Observer<ApiResponse<ResendOtpData>>() {
            @Override
            public void onChanged(ApiResponse<ResendOtpData> it) {
                handleResendOtpResult(it);
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        if (task.getResult() != null) {
                            String token = task.getResult().getToken();
                            sessionManager.setDEVICE_TOKEN(token);
                            Log.e(TAG, "token -" + token);
                        }
                    }
                });

    }

    @Override
    protected void setListeners() {
        binding.btnVerify.setOnClickListener(this);
        binding.tvResendOtp.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        Utility.hideKeyboard(mActivity);
        switch (view.getId()) {
            case R.id.btnVerify:
                verifyOtpAPI();
                break;
            case R.id.tvResendOtp:
                resendOtpAPI();
                break;
        }
    }

    private void getIntentData() {
        mBundle = getIntent().getBundleExtra("bundle");
        if (mBundle != null) {
            //otp = mBundle.getString("otp");
            userId = mBundle.getString("userId");
            mobile = mBundle.getString("mobile");
            //binding.otpView.setOTP(otp);
            binding.tvMobile.setText("+91-" + mobile);
        }
    }


    private void verifyOtpAPI() {
        String otp = binding.otpView.getOTP().trim();
            if (viewModel.isValidFormData(mActivity, otp)) {

                HashMap<String, String> reqData = new HashMap<>();
                reqData.put("userid", userId);
                reqData.put("otp", otp);

                if (Utility.isOnline(mActivity)) {
                    Log.e(TAG, "Api parameters - " + reqData.toString());
                    viewModel.verifyOtp(reqData);
                } else {
                    showNoInternetDialog();
                }
            }
    }

    private void handleResult(ApiResponse<VerifyOtpData> result) {
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

                    sessionManager.setFIREBASE_ID(result.getData().getUserData().getFirebaseId());
                    sessionManager.setLogin();
                    sessionManager.setSocial(false);
                    if (result.getData().getIsSend() == 1) {
                        sessionManager.setSelectBalloon(false); //select balloon screen
                    } else {
                        sessionManager.setSelectBalloon(true); //select balloon screen
                    }
                    sessionManager.setUSER_ID(result.getData().getUserData().getUserId());
                    sessionManager.setFULL_NAME(result.getData().getUserData().getName());
                    sessionManager.setPHONE(result.getData().getUserData().getPhone());
                    sessionManager.setADDRESS(result.getData().getUserData().getLocation());
                    sessionManager.setPROFILE_IMAGE(result.getData().getUserData().getProfileImage());
                    HomeActivity.startActivity(mActivity, null, true);
                    finish();

                } else {
                    Utility.showToastMessageError(mActivity, result.getData().getMessage());
                }
                break;
        }
    }

    private void resendOtpAPI() {
        if (userId != null && !userId.isEmpty()) {

            HashMap<String, String> reqData = new HashMap<>();
            reqData.put("userid", userId);
            reqData.put("deviceId", sessionManager.getDEVICE_TOKEN());

            if (Utility.isOnline(mActivity)) {
                Log.e(TAG, "Api parameters - " + reqData.toString());
                viewModel.resendOtp(reqData);
            } else {
                showNoInternetDialog();
            }
        }
    }

    private void handleResendOtpResult(ApiResponse<ResendOtpData> result) {
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

                    showSuccessfullyDialog(result.getData().getMessage());
                    //binding.otpView.setOTP(result.getData().getData().getOtp());
                    //otp = result.getData().getData().getOtp();

                } else {
                    Utility.showToastMessageError(mActivity, result.getData().getMessage());
                }
                break;
        }
    }

    private void showSuccessfullyDialog(String message) {
        final Dialog dialog = new Dialog(mActivity, R.style.Theme_Dialog);
        DialogSuccessfullyBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.dialog_successfully, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialogBinding.tvTitle.setText("Successfully");
        dialogBinding.tvMessage.setText(message);
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

    public static void startActivity(Activity activity, Bundle bundle, boolean isClear) {
        Intent intent = new Intent(activity, VerificationActivity.class);
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
