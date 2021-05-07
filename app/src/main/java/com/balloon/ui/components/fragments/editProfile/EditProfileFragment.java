package com.balloon.ui.components.fragments.editProfile;

import android.annotation.SuppressLint;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.asksira.bsimagepicker.BSImagePicker;
import com.balloon.R;
import com.balloon.databinding.DialogSuccessfullyBinding;
import com.balloon.databinding.FragmentChatBinding;
import com.balloon.databinding.FragmentEditProfileBinding;
import com.balloon.network.ApiResponse;
import com.balloon.pojo.EditProfileData;
import com.balloon.pojo.LoginData;
import com.balloon.pojo.ProfileData;
import com.balloon.ui.base.BaseFragment;
import com.balloon.ui.components.activities.home.HomeActivity;
import com.balloon.ui.components.activities.login.LoginActivity;
import com.balloon.ui.components.activities.settings.SettingActivity;
import com.balloon.ui.components.activities.verification.VerificationActivity;
import com.balloon.utils.Constants;
import com.balloon.utils.EmojiExcludeFilter;
import com.balloon.utils.ProgressDialog;
import com.balloon.utils.Utility;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

import static android.app.Activity.RESULT_OK;


public class EditProfileFragment extends BaseFragment implements BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.OnMultiImageSelectedListener,
        BSImagePicker.ImageLoaderDelegate {
    private static final String TAG = EditProfileFragment.class.getName();
    private FragmentEditProfileBinding binding;
    private EditProfileViewModel viewModel;
    private View.OnClickListener onClickListener = null;
    private HomeActivity homeActivity;

    public static String selectedImagePath = "";
    private static final int REQ_CODE_GALLERY_PICKER3 = 30;
    private File mFileTemp = new File("");
    public static final String TEMP_PHOTO_FILE_NAME = "GoTo.png";
    private String uploadImg = "Upload Image";


    @Override
    protected ViewDataBinding setBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false);
        onClickListener = this;
        binding.setLifecycleOwner(this);
        return binding;
    }

    @Override
    protected void createActivityObject() {
        mActivity = (AppCompatActivity) getActivity();
    }

    @Override
    protected void initializeObject() {
        binding.tvTitle.setText("Edit Profile");
        binding.etName.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        binding.etLocation.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

    }


    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = mActivity.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);

        Log.e(TAG, "check getProfile - " + sessionManager.getPROFILE_IMAGE());
        Log.e(TAG, "check selectImage - " + selectedImagePath);

        if (sessionManager.getFULL_NAME().isEmpty() || sessionManager.getPHONE().isEmpty() || sessionManager.getADDRESS().isEmpty()
                || sessionManager.getPROFILE_IMAGE().isEmpty()) {
            getProfileApi();
        } else {
            setProfileData();
        }

    }

    private void setProfileData() {
        binding.etName.setText(sessionManager.getFULL_NAME());
        binding.etPhone.setText(sessionManager.getPHONE());
        binding.etLocation.setText(sessionManager.getADDRESS());
        binding.etBio.setText(sessionManager.getBIO());
        if (selectedImagePath.isEmpty()) {
            if (sessionManager.getPROFILE_IMAGE() != null && !sessionManager.getPROFILE_IMAGE().isEmpty())
            Utility.loadImage(binding.ivProfile, sessionManager.getPROFILE_IMAGE());
        }
    }


    @Override
    protected void initializeOnCreateObject() {
        homeActivity = (HomeActivity) getActivity();
        viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
        viewModel.responseLiveData.observe(this, new Observer<ApiResponse<EditProfileData>>() {
            @Override
            public void onChanged(ApiResponse<EditProfileData> it) {
                handleResult(it);
            }
        });
        viewModel.responseLiveProfileData.observe(this, new Observer<ApiResponse<ProfileData>>() {
            @Override
            public void onChanged(ApiResponse<ProfileData> it) {
                handleProfileResult(it);
            }
        });
    }


    @Override
    protected void setListeners() {
        binding.ivBack.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
        binding.tvEdit.setOnClickListener(this);
        binding.btnSetting.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        Utility.hideKeyboard(mActivity);
        switch (view.getId()) {
            case R.id.ivBack:
                homeActivity.onBackPressed();
                break;
            case R.id.btnSubmit:
                editProfileAPI();
                break;
            case R.id.tvEdit:
                createAppDir();
                BSImagePicker pickerDialog = new BSImagePicker.Builder("com.balloon.fileProvider")
                        .build();
                pickerDialog.show(getChildFragmentManager(), "picker");
                break;
            case R.id.btnSetting:
                SettingActivity.startActivity(mActivity,null,false);
                break;
        }
    }

    private void getProfileApi() {
        if (sessionManager.getUSER_ID() !=null && !sessionManager.getUSER_ID().isEmpty()) {

            HashMap<String, String> reqData = new HashMap<>();
            reqData.put("userId", sessionManager.getUSER_ID());

            if (Utility.isOnline(mActivity)) {
                Log.e(TAG, "Api parameters - " + reqData.toString());
                viewModel.getProfile(reqData);
            } else {
                showNoInternetDialog();
            }
        }
    }

    private void handleProfileResult(ApiResponse<ProfileData> result) {
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

                    if (result.getData().getData().getUserinfo() !=null) {
                        sessionManager.setUSER_ID(result.getData().getData().getUserId());
                        sessionManager.setFULL_NAME(result.getData().getData().getUserinfo().getName());
                        sessionManager.setPROFILE_IMAGE(result.getData().getData().getUserinfo().getImage());
                        sessionManager.setPHONE(result.getData().getData().getUserinfo().getPhone());
                        sessionManager.setADDRESS(result.getData().getData().getUserinfo().getLocation());
                        sessionManager.setBIO(result.getData().getData().getUserinfo().getBio());

                        setProfileData();
                    }

                } else {
                    Utility.showToastMessageError(mActivity, result.getData().getMessage());
                }
                break;
        }
    }


    private void editProfileAPI() {
        String name = binding.etName.getText().toString().trim();
        String location = binding.etLocation.getText().toString().trim();
        String bio = binding.etBio.getText().toString().trim();

        if (uploadImg.equals("Upload Image")) {
            if (sessionManager.getPROFILE_IMAGE() != null && !sessionManager.getPROFILE_IMAGE().isEmpty())
            uploadImg = sessionManager.getPROFILE_IMAGE();
        }
        Log.e(TAG,"check - "+uploadImg);

        if (viewModel.isValidFormData(mActivity, uploadImg, name, location,bio)) {

            HashMap<String, String> reqData = new HashMap<>();

            reqData.put("userId", sessionManager.getUSER_ID());
            reqData.put("name", name);
            reqData.put("location", location);
            reqData.put("about", bio);
            /*if (selectedImagePath != null)
                reqData.put("profile", selectedImagePath);
            else
                reqData.put("profile", "");*/

            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFileTemp);

            MultipartBody.Part req_userId = MultipartBody.Part.createFormData("userId", sessionManager.getUSER_ID());
            MultipartBody.Part req_name = MultipartBody.Part.createFormData("name", name);
            MultipartBody.Part req_location = MultipartBody.Part.createFormData("location", location);
            MultipartBody.Part req_bio = MultipartBody.Part.createFormData("about", bio);
            MultipartBody.Part profile_photo = null;
            if (selectedImagePath.isEmpty()) {
            } else {
                profile_photo = MultipartBody.Part.createFormData("profile", mFileTemp.getName(), requestBody);
            }

            if (Utility.isOnline(mActivity)) {
                Log.e(TAG, "Api parameters - " + reqData.toString());
                Log.e(TAG, "profile - " + profile_photo);
                viewModel.editProfile(req_userId,req_name, req_location, profile_photo,req_bio);
            } else {
                showNoInternetDialog();
            }
        }
    }

    private void handleResult(ApiResponse<EditProfileData> result) {
        switch (result.getStatus()) {
            case ERROR:
                ProgressDialog.hideProgressDialog();
                Utility.showToastMessageError(mActivity, result.getError().getMessage());
                Log.e(TAG, "error - " + result.getError().getMessage());
                break;
            case LOADING:
                ProgressDialog.showProgressDialog(mActivity);
                break;
            case SUCCESS:
                ProgressDialog.hideProgressDialog();
                if (result.getData().getStatusCode() == Constants.Success) {
                    Log.e(TAG, "Response - " + new Gson().toJson(result));

                    sessionManager.setUSER_ID(result.getData().getUserData().getUserId());
                    sessionManager.setFULL_NAME(result.getData().getUserData().getName());
                    sessionManager.setPROFILE_IMAGE(result.getData().getUserData().getProfileImage());
                    sessionManager.setPHONE(result.getData().getUserData().getPhone());
                    sessionManager.setADDRESS(result.getData().getUserData().getLocation());
                   sessionManager.setBIO(result.getData().getUserData().getBio());
                    setProfileData();

                    showSuccessfullyDialog();

                } else {
                    Utility.showToastMessageError(mActivity, result.getData().getMessage());
                    Log.e(TAG, "error failure - " + result.getData().getMessage());
                }
                break;
        }
    }

    private void showSuccessfullyDialog() {
        final Dialog dialog = new Dialog(mActivity, R.style.Theme_Dialog);
        DialogSuccessfullyBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.dialog_successfully, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialogBinding.tvTitle.setText("Successfully");
        dialogBinding.tvMessage.setText("User Profile successfully updated.");
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
                homeActivity.onBackPressed();
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


    @Override
    public void loadImage(Uri imageUri, ImageView ivImage) {
        Glide.with(mActivity).load(imageUri).into(ivImage);

    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {

    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        selectedImagePath = uri.getPath();
        uploadImg = selectedImagePath;

        InputStream inputStream = null;
        try {
            inputStream = mActivity.getContentResolver().openInputStream(uri);
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
                    .start(mActivity,this, REQ_CODE_GALLERY_PICKER3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQ_CODE_GALLERY_PICKER3) {
            final Uri resultUri = UCrop.getOutput(data);
            selectedImagePath = resultUri.getPath();
            Bitmap bitmap = Utility.decodeUriToBitmap(mActivity, resultUri);
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
            mFileTemp = new File(root + "/" + getString(R.string.app_name) + "/temp/", new Date().getTime()+TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(mActivity.getFilesDir(), new Date().getTime()+TEMP_PHOTO_FILE_NAME);
        }
    }

    @Override
    public void onDestroy() {
        viewModel.disposeSubscriber();
        selectedImagePath = "";
        super.onDestroy();
    }
}