package com.balloon.ui.components.activities.splash;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.balloon.R;
import com.balloon.databinding.ActivitySplashBinding;
import com.balloon.databinding.DialogLogoutBinding;
import com.balloon.databinding.DialogSuccessBinding;
import com.balloon.ui.base.BaseBindingActivity;
import com.balloon.ui.components.activities.home.HomeActivity;
import com.balloon.ui.components.activities.loginOption.LoginOptionActivity;
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

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.balloon.utils.SingleShotLocationProvider.requestSingleUpdate;

public class SplashActivity extends BaseBindingActivity {
    private static final String TAG = SplashActivity.class.getName();
    private ActivitySplashBinding binding;
    private SessionManager sessionManager;
    private static final int RequestPermissionCode = 1;
    public static final int REQUEST_CHECK_SETTINGS = 123;


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
                            //goNextScreen();
                            checkWhetherLocationSettingsAreSatisfied();
                        } else {
                            requestPermission();
                        }
                    } else {
                        //goNextScreen();
                        checkWhetherLocationSettingsAreSatisfied();
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
            //goNextScreen();
            checkWhetherLocationSettingsAreSatisfied();
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
        if (SessionManager.getInstance(SplashActivity.this).isLogin())
            HomeActivity.startActivity(mActivity, null, true);
        else
            //LoginActivity.startActivity(this, null, true);
            LoginOptionActivity.startActivity(mActivity, null, true);
        finish();
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
                                goNextScreen();
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
                                goNextScreen();
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

}
