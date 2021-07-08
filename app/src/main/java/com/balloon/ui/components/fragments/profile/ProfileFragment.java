package com.balloon.ui.components.fragments.profile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.asksira.bsimagepicker.BSImagePicker;
import com.balloon.R;
import com.balloon.databinding.DialogImageViewBinding;
import com.balloon.databinding.DialogSuccessfullyBinding;
import com.balloon.databinding.FragmentProfileBinding;
import com.balloon.network.ApiResponse;
import com.balloon.pojo.ProfileData;
import com.balloon.pojo.UploadImageData;
import com.balloon.ui.base.BaseFragment;
import com.balloon.ui.components.activities.home.HomeActivity;
import com.balloon.ui.components.adapters.PhotosAdapter;
import com.balloon.ui.components.fragments.home.HomeFragment;
import com.balloon.ui.components.fragments.settings.SettingsFragment;
import com.balloon.utils.Constants;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends BaseFragment implements BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.OnMultiImageSelectedListener,
        BSImagePicker.ImageLoaderDelegate {
    private static final String TAG = ProfileFragment.class.getName();
    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private View.OnClickListener onClickListener = null;
    private HomeActivity homeActivity;
    private ArrayList<ProfileData.Data.Userinfo.ImagesItem> photoData;
    private PhotosAdapter photosAdapter;

    private String selectedImagePath = "";
    private File mFileTemp = new File("");

    private static final int REQ_CODE_GALLERY_PICKER3 = 30;
    public static final String TEMP_PHOTO_FILE_NAME = "GoTo.png";

    // Animation
    Animation animMoveToTop;


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

        // load the animation
        animMoveToTop = AnimationUtils.loadAnimation(mActivity, R.anim.move);
        //animMoveToTop.setStartOffset(500);
    }

    private void setProfileData(ProfileData.Data.Userinfo userinfo) {
        Utility.loadPicture(binding.ivProfile, userinfo.getImage());
        binding.tvFullName.setText(userinfo.getName());

        if(userinfo.getRating() !=null && !userinfo.getRating().equals("")) {

            if (Double.parseDouble(userinfo.getRating()) > 75 && Double.parseDouble(userinfo.getRating()) <= 100)
            {
                setRelativeBalloonVisibility(View.GONE,View.GONE,View.GONE,View.VISIBLE);
                binding.rlBalloon4.startAnimation(animMoveToTop);
                binding.tvAirPercentage4.setText(userinfo.getRating().indexOf(".") < 0 ? userinfo.getRating()+"%" : userinfo.getRating()
                        .replaceAll("0*$", "").replaceAll("\\.$", "") + "%");
            }else if (Double.parseDouble(userinfo.getRating()) > 50 && Double.parseDouble(userinfo.getRating())<=75)
            {
                setRelativeBalloonVisibility(View.GONE,View.GONE,View.VISIBLE,View.GONE);
                binding.rlBalloon3.startAnimation(animMoveToTop);
                binding.tvAirPercentage3.setText(userinfo.getRating().indexOf(".") < 0 ? userinfo.getRating()+"%" : userinfo.getRating()
                        .replaceAll("0*$", "").replaceAll("\\.$", "") + "%");
            }else if (Double.parseDouble(userinfo.getRating()) > 25 && Double.parseDouble(userinfo.getRating())<=50)
            {
                setRelativeBalloonVisibility(View.GONE,View.VISIBLE,View.GONE,View.GONE);
                binding.rlBalloon2.startAnimation(animMoveToTop);
                binding.tvAirPercentage2.setText(userinfo.getRating().indexOf(".") < 0 ? userinfo.getRating()+"%" : userinfo.getRating()
                        .replaceAll("0*$", "").replaceAll("\\.$", "") + "%");
            }else{
                setRelativeBalloonVisibility(View.VISIBLE,View.GONE,View.GONE,View.GONE);
                binding.rlBalloon1.startAnimation(animMoveToTop);
                binding.tvAirPercentage1.setText(userinfo.getRating().indexOf(".") < 0 ? userinfo.getRating()+"%" : userinfo.getRating()
                        .replaceAll("0*$", "").replaceAll("\\.$", "") + "%");
            }
        }else{
            setRelativeBalloonVisibility(View.VISIBLE,View.GONE,View.GONE,View.GONE);
            binding.tvAirPercentage1.setText("0%");
        }
        if(userinfo.getRating() !=null && !userinfo.getRating().equals("")) {
            binding.progressViewAir.setProgress(Float.parseFloat(userinfo.getRating()));
        }
        else
        {
            binding.progressViewAir.setProgress(Float.parseFloat("0"));
        }

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

    private void setRelativeBalloonVisibility(int v1, int v2, int v3, int v4) {
        binding.relativeBalloon1.setVisibility(v1);
        binding.relativeBalloon2.setVisibility(v2);
        binding.relativeBalloon3.setVisibility(v3);
        binding.relativeBalloon4.setVisibility(v4);
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
        binding.btnSettings.setOnClickListener(this);
        binding.btnMyBio.setOnClickListener(this);
        binding.btnPhoto.setOnClickListener(this);
        /*binding.progressViewAir.setOnProgressChangeListener(new OnProgressChangeListener() {
            @Override
            public void onChange(float v) {
                binding.progressViewAir.setLabelText("air"+v);
            }
        });*/
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                homeActivity.onBackPressed();
                break;
            case R.id.btnSettings:
                homeActivity.changeFragment(new SettingsFragment(), true);
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
                if (position == photoData.size() - 1) {
                    createAppDir();
                    BSImagePicker pickerDialog = new BSImagePicker.Builder("com.balloon.fileProvider")
                            .build();
                    pickerDialog.show(getChildFragmentManager(), "picker");
                } else {
                    showImageViewDialog(Constants.BASE_IMG_URL + photoData.get(position).getImage());
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
        if (sessionManager.getUSER_ID() != null && !sessionManager.getUSER_ID().isEmpty()) {

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

                    if (result.getData().getData().getUserinfo() != null) {
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
        if (sessionManager.getUSER_ID() != null && !sessionManager.getUSER_ID().isEmpty()) {

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
                viewModel.uploadImage(req_userId, req_image);
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
       /* selectedImagePath = Utility.getRealPathFromURI(mActivity, uri);
        mFileTemp = new File(selectedImagePath);*/

        selectedImagePath = Utility.getRealPathFromURI(mActivity, uri);
        //selectedImagePath = ImagePathUtility.getImagePath(mActivity, uri);
        //mFileTemp = new File(selectedImagePath);
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
                    .withAspectRatio(0, 0)
                    .start(mActivity, this, REQ_CODE_GALLERY_PICKER3);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //uploadImageAPI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        Log.e(TAG, "result - " + UCrop.getOutput(data));

        if (requestCode == REQ_CODE_GALLERY_PICKER3) {
            final Uri resultUri = UCrop.getOutput(data);
            selectedImagePath = resultUri.getPath();
            Bitmap bitmap = Utility.decodeUriToBitmap(mActivity, resultUri);
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bOut);
            selectedImagePath = Base64.encodeToString(bOut.toByteArray(), Base64.DEFAULT);

            //binding.ivProfile.setImageURI(resultUri);
            uploadImageAPI();
        }
    }

    private void createAppDir() {
        String root = Environment.getExternalStorageDirectory().toString();
        new File(root + "/" + getString(R.string.app_name) + "/temp").mkdirs();
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(root + "/" + getString(R.string.app_name) + "/temp/", new Date().getTime() + TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(mActivity.getFilesDir(), new Date().getTime() + TEMP_PHOTO_FILE_NAME);
        }
    }

    private void showImageViewDialog(String img_url) {
        final Dialog dialog = new Dialog(mActivity, R.style.Theme_Dialog);
        DialogImageViewBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.dialog_image_view, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(dialogBinding.getRoot());
        dialogBinding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        Utility.loadImage(dialogBinding.mBigImage, img_url);
        dialog.show();
    }

    @Override
    public void onDestroy() {
        viewModel.disposeSubscriber();
        selectedImagePath = "";
        super.onDestroy();
    }
}