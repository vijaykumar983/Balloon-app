package com.balloon.ui.components.activities.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.asksira.bsimagepicker.BSImagePicker;
import com.balloon.R;
import com.balloon.databinding.ActivityLoginBinding;
import com.balloon.databinding.DialogSuccessBinding;
import com.balloon.databinding.DialogSuccessfullyBinding;
import com.balloon.network.ApiResponse;
import com.balloon.pojo.LoginData;
import com.balloon.ui.base.BaseBindingActivity;
import com.balloon.ui.components.activities.verification.VerificationActivity;
import com.balloon.utils.Constants;
import com.balloon.utils.EmojiExcludeFilter;
import com.balloon.utils.ProgressDialog;
import com.balloon.utils.SessionManager;
import com.balloon.utils.SingleShotLocationProvider;
import com.balloon.utils.Utility;
import com.bumptech.glide.Glide;
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

import static com.balloon.utils.SingleShotLocationProvider.requestSingleUpdate;


public class LoginActivity extends BaseBindingActivity implements BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.OnMultiImageSelectedListener,
        BSImagePicker.ImageLoaderDelegate {
    private static final String TAG = LoginActivity.class.getName();
    private ActivityLoginBinding binding;
    private LoginViewModel viewModel = null;

    public static String selectedImagePath = "";
    private static final int REQ_CODE_GALLERY_PICKER3 = 30;
    private File mFileTemp;
    public static final String TEMP_PHOTO_FILE_NAME = "GoTo.png";
    private String userStatus;
    public static final int REQUEST_CHECK_SETTINGS = 123;


    @Override
    protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setLifecycleOwner(this);
    }

    @Override
    protected void createActivityObject(@Nullable Bundle savedInstanceState) {
        mActivity = this;
    }

    @SuppressLint("ResourceType")
    @Override
    protected void initializeObject() {
        binding.etName.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        binding.etLocation.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        viewModel.responseLiveData.observe(this, new Observer<ApiResponse<LoginData>>() {
            @Override
            public void onChanged(ApiResponse<LoginData> it) {
                handleResult(it);
            }
        });

        //checkWhetherLocationSettingsAreSatisfied();
    }

    @Override
    protected void setListeners() {
        binding.btnNext.setOnClickListener(this);
        binding.tvEdit.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        Utility.hideKeyboard(mActivity);
        switch (view.getId()) {
            case R.id.btnNext:
                loginAPI();
                //VerificationActivity.startActivity(mActivity, null, false);
                break;
            case R.id.tvEdit:
                createAppDir();
                BSImagePicker pickerDialog = new BSImagePicker.Builder("com.balloon.fileProvider")
                        .build();
                pickerDialog.show(getSupportFragmentManager(), "picker");
                break;

        }
    }

    /*private void checkWhetherLocationSettingsAreSatisfied() {

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
                               binding.etLocation.setText(sessionManager.getLOCATION());
                                binding.etLocation.setSelected(true);
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
    }*/

    @Override
    public void onResume() {
        super.onResume();
        binding.etLocation.setText(sessionManager.getLOCATION());
        binding.etLocation.setSelected(true);
    }

    private void loginAPI() {
        String name = binding.etName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String location = binding.etLocation.getText().toString().trim();

        if (viewModel.isValidFormData(mActivity, selectedImagePath, name, phone, location)) {

            HashMap<String, String> reqData = new HashMap<>();

            reqData.put("name", name);
            reqData.put("mobileNo", phone);
            reqData.put("location", location);
            reqData.put("deviceId", "1342423");
            /*if (selectedImagePath != null)
                reqData.put("image", selectedImagePath);
            else
                reqData.put("image", "");*/

            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFileTemp);

            MultipartBody.Part req_name = MultipartBody.Part.createFormData("name", name);
            MultipartBody.Part req_phone = MultipartBody.Part.createFormData("mobileNo", phone);
            MultipartBody.Part req_location = MultipartBody.Part.createFormData("location", location);
            MultipartBody.Part req_deviceId = MultipartBody.Part.createFormData("deviceId", "1342423");
            MultipartBody.Part profile_photo = null;
            if (selectedImagePath.isEmpty()) {
            } else {
                profile_photo = MultipartBody.Part.createFormData("profile", mFileTemp.getName(), requestBody);
            }

            if (Utility.isOnline(mActivity)) {
                Log.e(TAG, "Api parameters - " + reqData.toString());
                Log.e(TAG, "profile - " + profile_photo);
                viewModel.login(req_name, req_phone, req_location, req_deviceId, profile_photo);
            } else {
                showNoInternetDialog();
            }
        }
    }

    private void handleResult(ApiResponse<LoginData> result) {
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
                if (result.getData().getStatusCode() == Constants.Success) {
                    Log.e(TAG, "Response - " + new Gson().toJson(result));

                    Bundle bundle = new Bundle();
                    bundle.putString("userId", result.getData().getData().getUserId());
                    bundle.putString("otp", String.valueOf(result.getData().getData().getOtp()));
                    bundle.putString("mobile", binding.etPhone.getText().toString().trim());
                    bundle.putString("userStatus", userStatus);
                    VerificationActivity.startActivity(mActivity, bundle, false);

                } else {
                    Utility.showToastMessageError(mActivity, result.getData().getMessage());
                    Log.e(TAG, "error failure - " + result.getError().getMessage());
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


    @Override
    public void loadImage(Uri imageUri, ImageView ivImage) {
        Glide.with(LoginActivity.this).load(imageUri).into(ivImage);

    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {

    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        selectedImagePath = uri.getPath();

        InputStream inputStream = null;
        try {
            inputStream = LoginActivity.this.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(mFileTemp);

            Utility.copyStream(inputStream, fileOutputStream);

            fileOutputStream.close();
            inputStream.close();
            UCrop.of(Uri.fromFile(mFileTemp), Uri.fromFile(mFileTemp))
                    .withAspectRatio(4, 4)
                    .start(LoginActivity.this, REQ_CODE_GALLERY_PICKER3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == REQUEST_CHECK_SETTINGS) {
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
                                binding.etLocation.setText(sessionManager.getLOCATION());
                                binding.etLocation.setSelected(true);
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
        }*/

        if (requestCode == REQ_CODE_GALLERY_PICKER3) {
            final Uri resultUri = UCrop.getOutput(data);
            selectedImagePath = resultUri.getPath();
            Bitmap bitmap = Utility.decodeUriToBitmap(LoginActivity.this, resultUri);
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bOut);
            selectedImagePath = Base64.encodeToString(bOut.toByteArray(), Base64.DEFAULT);

            binding.ivProfile.setImageURI(resultUri);
        }

    }

    private void createAppDir() {
        String root = Environment.getExternalStorageDirectory().toString();
        new File(root + "/" + getString(R.string.app_name) + "/temp").mkdirs();
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(root + "/" + getString(R.string.app_name) + "/temp/", new Date().getTime() + TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(LoginActivity.this.getFilesDir(), new Date().getTime() + TEMP_PHOTO_FILE_NAME);
        }
    }

    public static void startActivity(Activity activity, Bundle bundle, boolean isClear) {
        Intent intent = new Intent(activity, LoginActivity.class);
        if (bundle != null) intent.putExtra("bundle", bundle);
        if (isClear)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    @Override
    public void onDestroy() {
        viewModel.disposeSubscriber();
        selectedImagePath = "";
        super.onDestroy();
    }
}
