package com.balloon.ui.components.activities.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.asksira.bsimagepicker.BSImagePicker;
import com.balloon.R;
import com.balloon.databinding.ActivityLoginBinding;
import com.balloon.databinding.ActivitySettingBinding;
import com.balloon.databinding.DialogSuccessfullyBinding;
import com.balloon.network.ApiResponse;
import com.balloon.pojo.LoginData;
import com.balloon.ui.base.BaseBindingActivity;
import com.balloon.ui.components.activities.login.LoginViewModel;
import com.balloon.ui.components.activities.verification.VerificationActivity;
import com.balloon.utils.Constants;
import com.balloon.utils.EmojiExcludeFilter;
import com.balloon.utils.ProgressDialog;
import com.balloon.utils.Utility;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class SettingActivity extends BaseBindingActivity {
    private static final String TAG = SettingActivity.class.getName();
    private ActivitySettingBinding binding;



    @Override
    protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        binding.setLifecycleOwner(this);
    }

    @Override
    protected void createActivityObject(@Nullable Bundle savedInstanceState) {
        mActivity = this;
    }

    @SuppressLint("ResourceType")
    @Override
    protected void initializeObject() {
        binding.appBar.tvTitle.setText("Settings");
    }

    @Override
    protected void setListeners() {
        binding.appBar.ivBack.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        Utility.hideKeyboard(mActivity);
        switch (view.getId()) {
            case R.id.ivBack:
                finish();
                break;

        }
    }


    public static void startActivity(Activity activity, Bundle bundle, boolean isClear) {
        Intent intent = new Intent(activity, SettingActivity.class);
        if (bundle != null) intent.putExtra("bundle", bundle);
        if (isClear)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

}
