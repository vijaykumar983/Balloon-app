package com.balloon.ui.components.activities.loginOption;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.balloon.R;
import com.balloon.databinding.ActivityLoginOptionBinding;
import com.balloon.ui.base.BaseBindingActivity;
import com.balloon.ui.components.activities.login.LoginActivity;
import com.balloon.utils.Utility;
import com.google.gson.Gson;

import java.util.HashMap;


public class LoginOptionActivity extends BaseBindingActivity {
    private static final String TAG = LoginOptionActivity.class.getName();
    private ActivityLoginOptionBinding binding;
    private LoginOptionViewModel viewModel = null;


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
        /*viewModel.responseLiveData.observe(this, new Observer<ApiResponse<LoginSignupData>>() {
            @Override
            public void onChanged(ApiResponse<LoginSignupData> it) {
                handleResult(it);
            }
        });*/
    }

    @Override
    protected void setListeners() {
        binding.tvLoginPhone.setOnClickListener(this);
        binding.tvLoginFacebook.setOnClickListener(this);
        binding.tvLoginGoogle.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        Utility.hideKeyboard(mActivity);
        switch (view.getId()) {
            case R.id.tvLoginPhone:
                LoginActivity.startActivity(mActivity, null, false);
                break;
            case R.id.tvLoginFacebook:

                break;
            case R.id.tvLoginGoogle:

                break;

        }
    }

    /*private void loginSignupApi() {
        String mobile = binding.edtEmialPhone.getText().toString().trim();

        if (viewModel.isValidFormData(mActivity, mobile)) {

            HashMap<String, String> reqData = new HashMap<>();
            reqData.put("phone", mobile);

            Log.e(TAG, "Api parameters - " + reqData.toString());
            viewModel.login_signup(reqData);
        }
    }

    private void handleResult(ApiResponse<LoginSignupData> result) {
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
                        nextScreen(result);

                    } else if (result.getData().getStatusCode() == 201) {
                        nextScreen(result);
                    } else {
                        Utility.showToastMessageError(mActivity, result.getData().getMessage());
                    }
                break;
        }
    }

    private void nextScreen(ApiResponse<LoginSignupData> result) {
        Bundle bundle = new Bundle();
        bundle.putString("userId", result.getData().getData().getUserId());
        bundle.putString("otp", result.getData().getData().getOtp());
        bundle.putString("mobile", binding.edtEmialPhone.getText().toString().trim());
        VerifyOtpActivity.startActivity(mActivity, bundle, false);
        finish();
    }*/


    public static void startActivity(Activity activity, Bundle bundle, boolean isClear) {
        Intent intent = new Intent(activity, LoginOptionActivity.class);
        if (bundle != null) intent.putExtra("bundle", bundle);
        if (isClear)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    @Override
    public void onDestroy() {
        //viewModel.disposeSubscriber();
        super.onDestroy();
    }
}
