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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;


public class VerificationActivity extends BaseBindingActivity {
    private static final String TAG = VerificationActivity.class.getName();
    private ActivityVerificationBinding binding;
    private VerificationViewModel viewModel = null;
    private Bundle mBundle;
    private String otp = "", userId = "", mobile = "", userStatus = "";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


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
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
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
            otp = mBundle.getString("otp");
            userId = mBundle.getString("userId");
            mobile = mBundle.getString("mobile");
            userStatus = mBundle.getString("userStatus");
            binding.otpView.setOTP(otp);
            binding.tvMobile.setText("+91-" + mobile);
        }
    }


    private void verifyOtpAPI() {
        String otp = binding.otpView.getOTP().trim();
        if (userId != null && !userId.isEmpty() && otp != null && !otp.isEmpty()) {
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
                //ProgressDialog.hideProgressDialog();
                Log.e(TAG, "Response - " + new Gson().toJson(result));
                if (result.getData().getStatusCode() == Constants.Success) {

                    if (userStatus != null && userStatus.equalsIgnoreCase("new")) {
                        firebaseSignup(result.getData().getUserData().getUserId(), result.getData().getUserData().getPhone() +
                                        "@gmail.com", "123456", result.getData().getUserData().getName(),
                                result.getData().getUserData().getProfileImage(), result.getData().getUserData().getPhone()
                                , result.getData().getUserData().getLocation());
                    } else {
                        loginFirebase(result.getData().getUserData().getUserId(),result.getData().getUserData().getPhone() +
                                        "@gmail.com", "123456", result.getData().getUserData().getName(),
                                result.getData().getUserData().getProfileImage(), result.getData().getUserData().getPhone()
                                , result.getData().getUserData().getLocation());
                    }


                } else {
                    ProgressDialog.hideProgressDialog();
                    Utility.showToastMessageError(mActivity, result.getData().getMessage());
                }
                break;
        }
    }

    private void firebaseSignup(String userId, String email, String password, String name, String profile, String phone, String address) {
        //ProgressDialog.showProgressDialog(mActivity);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //  Log.e(TAG,"task-"+task.getResult().toString());

                //------IF USER IS SUCCESSFULLY REGISTERED-----
                if (task.isSuccessful()) {
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String user_id = current_user.getUid();
                    String token_id = FirebaseInstanceId.getInstance().getToken();
                    Map userMap = new HashMap();
                    userMap.put("device_token", token_id);
                    userMap.put("name", name);
                    userMap.put("status", "Hello Kshitiz");
                    userMap.put("image", profile);
                    userMap.put("thumb_image", profile);
                    userMap.put("online", "true");
                    mDatabase.child(user_id).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task1) {
                            if (task1.isSuccessful()) {
                                ProgressDialog.hideProgressDialog();
                                sessionManager.setFIREBASE_ID(user_id);
                                sessionManager.setLogin();
                                sessionManager.setUSER_ID(userId);
                                sessionManager.setFULL_NAME(name);
                                sessionManager.setPHONE(phone);
                                sessionManager.setADDRESS(address);
                                sessionManager.setPROFILE_IMAGE(profile);
                                HomeActivity.startActivity(mActivity, null, true);
                                finish();
                            } else {
                                ProgressDialog.hideProgressDialog();
                                Utility.showSnackBarMsgError(mActivity, "YOUR NAME IS NOT REGISTERED... MAKE NEW ACCOUNT-- ");
                            }

                        }
                    });
                } else {
                    ProgressDialog.hideProgressDialog();
                    Utility.showSnackBarMsgError(mActivity, task.getException().getMessage() + "");
                }
            }
        });
    }

    private void loginFirebase(String userId,String email, String password, String name, String profile, String phone, String address) {
        //ProgressDialog.showProgressDialog(mActivity);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(mActivity,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String user_id = mAuth.getCurrentUser().getUid();
                            String token_id = FirebaseInstanceId.getInstance().getToken();
                            Log.d("firbaseid", user_id);

                            HashMap addValue = new HashMap();
                            addValue.put("device_token", token_id);
                            addValue.put("name", name);
                            addValue.put("status", "Hello Kshitiz");
                            addValue.put("image", profile);
                            addValue.put("thumb_image", profile);
                            addValue.put("online", "true");

                            mDatabase.child(user_id).updateChildren(addValue, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        ProgressDialog.hideProgressDialog();
                                        sessionManager.setFIREBASE_ID(user_id);
                                        sessionManager.setLogin();
                                        sessionManager.setUSER_ID(userId);
                                        sessionManager.setFULL_NAME(name);
                                        sessionManager.setPHONE(phone);
                                        sessionManager.setADDRESS(address);
                                        sessionManager.setPROFILE_IMAGE(profile);
                                        HomeActivity.startActivity(mActivity, null, true);
                                        finish();

                                    } else {
                                        ProgressDialog.hideProgressDialog();
                                        Utility.showSnackBarMsgError(mActivity, databaseError.toString());
                                        Log.e("Error is : ", databaseError.toString());
                                    }
                                }
                            });

                        } else {
                            //ProgressDialog.hideProgressDialog();
                            //Utility.showSnackBarMsgError(mActivity, "Wrong Credentials");
                            firebaseSignup(userId, email, password,name, profile, phone, address);
                        }
                    }
                });
    }

    private void resendOtpAPI() {
        if (userId != null && !userId.isEmpty()) {

            HashMap<String, String> reqData = new HashMap<>();
            reqData.put("userid", userId);
            reqData.put("deviceId", "12345");

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
                    binding.otpView.setOTP(result.getData().getData().getOtp());
                    otp = result.getData().getData().getOtp();

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
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
        super.onDestroy();
    }


}
