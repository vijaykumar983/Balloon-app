package com.balloon.ui.components.fragments.profile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.asksira.bsimagepicker.BSImagePicker;
import com.balloon.R;
import com.balloon.databinding.DialogSuccessfullyBinding;
import com.balloon.databinding.FragmentProfileBinding;
import com.balloon.network.ApiResponse;
import com.balloon.pojo.EditProfileData;
import com.balloon.pojo.ProfileData;
import com.balloon.pojo.UploadImageData;
import com.balloon.ui.base.BaseFragment;
import com.balloon.ui.components.activities.home.HomeActivity;
import com.balloon.ui.components.adapters.PhotosAdapter;
import com.balloon.ui.components.fragments.editProfile.EditProfileFragment;
import com.balloon.utils.Constants;
import com.balloon.utils.ImagePathUtility;
import com.balloon.utils.ProgressDialog;
import com.balloon.utils.Utility;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.skydoves.progressview.OnProgressChangeListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class ProfileFragment extends BaseFragment implements BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.OnMultiImageSelectedListener,
        BSImagePicker.ImageLoaderDelegate{
    private static final String TAG = ProfileFragment.class.getName();
    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private View.OnClickListener onClickListener = null;
    private HomeActivity homeActivity;
    private ArrayList<ProfileData.Data.Userinfo.ImagesItem> photoData;
    private PhotosAdapter photosAdapter;

    private String selectedImagePath = "";
    private File mFileTemp = new File("");

    @Override
    protected ViewDataBinding setBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
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
        photoData = new ArrayList<>();
        getProfileApi();
    }

    private void setProfileData(ProfileData.Data.Userinfo userinfo) {
        Utility.loadImage(binding.ivProfile,userinfo.getImage());
        binding.tvFullName.setText(userinfo.getName());
        //binding.tvEmail.setText(sessionManager.getFULL_NAME());
        if (userinfo.getBio() != null && !userinfo.getBio().isEmpty()) {
            binding.tvBio.setVisibility(View.VISIBLE);
            binding.layoutError.rlerror.setVisibility(View.GONE);
            binding.tvBio.setText(userinfo.getBio());
        } else {
            binding.tvBio.setVisibility(View.GONE);
            binding.layoutError.rlerror.setVisibility(View.VISIBLE);
        }

        //if (userinfo.getImages() != null && !userinfo.getImages().isEmpty()) {
           // binding.rvPhotos.setVisibility(View.VISIBLE);
           // binding.layoutError1.rlerror.setVisibility(View.GONE);
            photoData.clear();
            photoData.addAll(userinfo.getImages());
            photoData.add(new ProfileData.Data.Userinfo.ImagesItem());
            if (photoData.size() != 0) {
                photosAdapter = new PhotosAdapter(mActivity, onClickListener, photoData);
                binding.rvPhotos.setAdapter(photosAdapter);
           }
        /*} else {
            binding.rvPhotos.setVisibility(View.GONE);
            binding.layoutError1.rlerror.setVisibility(View.VISIBLE);
        }*/
    }


    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = mActivity.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }


    @Override
    protected void initializeOnCreateObject() {
        homeActivity = (HomeActivity) getActivity();
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        viewModel.responseLiveData.observe(this, new Observer<ApiResponse<ProfileData>>() {
            @Override
            public void onChanged(ApiResponse<ProfileData> it) {
                handleResult(it);
            }
        });
        viewModel.responseLiveUploadImageData.observe(this, new Observer<ApiResponse<UploadImageData>>() {
            @Override
            public void onChanged(ApiResponse<UploadImageData> it) {
                handleUploadImageResult(it);
            }
        });
    }


    @Override
    protected void setListeners() {
        binding.ivBack.setOnClickListener(this);
        binding.btnEditProfile.setOnClickListener(this);
        binding.btnMyBio.setOnClickListener(this);
        binding.btnPhoto.setOnClickListener(this);
        binding.progressView.setOnProgressChangeListener(new OnProgressChangeListener() {
            @Override
            public void onChange(float v) {
                binding.progressView.setLabelText("air"+v);
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                homeActivity.onBackPressed();
                break;
            case R.id.btnEditProfile:
                homeActivity.changeFragment(new EditProfileFragment(), true);
                break;
            case R.id.btnMyBio:
                setBackground(binding.btnMyBio, binding.btnPhoto);
                binding.linearBio.setVisibility(View.VISIBLE);
                binding.linearPhotos.setVisibility(View.GONE);
                break;
            case R.id.btnPhoto:
                setBackground(binding.btnPhoto, binding.btnMyBio);
                binding.linearPhotos.setVisibility(View.VISIBLE);
                binding.linearBio.setVisibility(View.GONE);
                break;
            case R.id.rowPhotoItem:
                int position = (int) view.getTag();
                photosAdapter.selectedPos = position;
                if(position == photoData.size()-1)
                {
                    BSImagePicker pickerDialog = new BSImagePicker.Builder("com.balloon.fileProvider")
                                    .build();
                    pickerDialog.show(getChildFragmentManager(), "picker");
                }else
                {
                }
                break;
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void setBackground(AppCompatButton txt1, AppCompatButton txt2) {
        txt1.setTextColor(mActivity.getResources().getColor(R.color.colorWhite));
        txt1.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.bg_corner_rounded_btn_8dp));
        txt2.setTextColor(mActivity.getResources().getColor(R.color.colorBlack));
        txt2.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.bg_rounded_corner_white_8dp));
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

    private void handleResult(ApiResponse<ProfileData> result) {
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
                        sessionManager.setFULL_NAME(result.getData().getData().getUserinfo().getName());
                        sessionManager.setPROFILE_IMAGE(result.getData().getData().getUserinfo().getImage());
                        sessionManager.setPHONE(result.getData().getData().getUserinfo().getPhone());
                        sessionManager.setADDRESS(result.getData().getData().getUserinfo().getLocation());
                        setProfileData(result.getData().getData().getUserinfo());
                    }

                } else {
                    Utility.showToastMessageError(mActivity, result.getData().getMessage());
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

    private void uploadImageAPI() {
        if (sessionManager.getUSER_ID() !=null && !sessionManager.getUSER_ID().isEmpty()) {

            HashMap<String, String> reqData = new HashMap<>();

            reqData.put("userId", sessionManager.getUSER_ID());
            /*if (selectedImagePath != null)
                reqData.put("file[]", selectedImagePath);
            else
                reqData.put("file[]", "");*/

            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), mFileTemp);

            MultipartBody.Part req_userId = MultipartBody.Part.createFormData("userId", sessionManager.getUSER_ID());
            MultipartBody.Part req_image = null;
            if (selectedImagePath.isEmpty()) {
            } else {
                req_image = MultipartBody.Part.createFormData("file[]", mFileTemp.getName(), requestBody);
            }

            if (Utility.isOnline(mActivity)) {
                Log.e(TAG, "Api parameters - " + reqData.toString());
                Log.e(TAG, "image - " + req_image);
                viewModel.uploadImage(req_userId,req_image);
            } else {
                showNoInternetDialog();
            }
        }
    }

    private void handleUploadImageResult(ApiResponse<UploadImageData> result) {
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

                    getProfileApi();

                   // showSuccessfullyDialog();

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
        dialogBinding.tvMessage.setText("Image successfully uploaded.");
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
        selectedImagePath =  ImagePathUtility.getImagePath(mActivity,uri);
        mFileTemp = new File(selectedImagePath);
        uploadImageAPI();
    }

    @Override
    public void onDestroy() {
        viewModel.disposeSubscriber();
        selectedImagePath = "";
        super.onDestroy();
    }
}