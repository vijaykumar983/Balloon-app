package com.balloon.ui.components.activities.splash;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.balloon.R;
import com.balloon.databinding.ActivitySplashBinding;
import com.balloon.databinding.DialogLogoutBinding;
import com.balloon.ui.base.BaseBindingActivity;
import com.balloon.ui.components.activities.home.HomeActivity;
import com.balloon.ui.components.activities.loginOption.LoginOptionActivity;
import com.balloon.utils.SessionManager;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SplashActivity extends BaseBindingActivity {
    private static final String TAG = SplashActivity.class.getName();
    private ActivitySplashBinding binding;
    private SessionManager sessionManager;
    private static final int RequestPermissionCode = 1;


    @Override
    protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
    }

    @Override
    protected void createActivityObject(Bundle savedInstanceState) {
        mActivity = this;
    }

    @Override
    protected void initializeObject() {
        sessionManager = new SessionManager();
        startSplashTimer();
    }


    @Override
    protected void setListeners() {

    }

    private void startSplashTimer() {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkPermission()) {
                            goNextScreen();
                        } else {
                            requestPermission();
                        }
                    } else {
                        goNextScreen();
                    }
                }
            }, 3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
                && grantResults[3] == PackageManager.PERMISSION_GRANTED)
        //&& grantResults[4] == PackageManager.PERMISSION_GRANTED)
        {
            // now, you have permission go ahead
            goNextScreen();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                // || ActivityCompat.shouldShowRequestPermissionRationale(mActivity, READ_SMS)
            ) {
                // now, user has denied permission (but not permanently!)
                requestPermission();
            } else {
                // now, user has denied permission permanently!
                showPermissionDialog();
            }
        }
        return;

    }

    private void showPermissionDialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        DialogLogoutBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.dialog_logout, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(dialogBinding.getRoot());
        dialogBinding.tvTitle.setText(getResources().getString(R.string.permission_required));
        dialogBinding.tvMessage.setText(getResources().getString(R.string.you_have_forcefully) + " " + getResources().getString(R.string.for_this_action));
        dialogBinding.btnYes.setText("Settings");
        dialogBinding.btnNo.setText("Cancel");
        dialogBinding.btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });
        dialogBinding.btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", mActivity.getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        dialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        //requestPermissions(new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION, READ_SMS}, RequestPermissionCode);
        requestPermissions(new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION}, RequestPermissionCode);
    }

    private boolean checkPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(mActivity, CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(mActivity, WRITE_EXTERNAL_STORAGE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(mActivity, READ_EXTERNAL_STORAGE);
        int ForthPermissionResult = ContextCompat.checkSelfPermission(mActivity, ACCESS_FINE_LOCATION);
        //int FifthPermissionResult = ContextCompat.checkSelfPermission(mActivity, READ_SMS);
        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED && ThirdPermissionResult == PackageManager.PERMISSION_GRANTED
                && ForthPermissionResult == PackageManager.PERMISSION_GRANTED;
        // && FifthPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    private void goNextScreen() {
        if (SessionManager.getInstance(SplashActivity.this).isLogin()) {
            if (SessionManager.getInstance(SplashActivity.this).getSelectBalloon()) {
               // SelectBalloonActivity.startActivity(mActivity, null, true);
                HomeActivity.startActivity(mActivity, null, true);
            } else {
                HomeActivity.startActivity(mActivity, null, true);
            }
        } else {
            //LoginActivity.startActivity(this, null, true);
            LoginOptionActivity.startActivity(mActivity, null, true);
        }
        finish();
    }


}
