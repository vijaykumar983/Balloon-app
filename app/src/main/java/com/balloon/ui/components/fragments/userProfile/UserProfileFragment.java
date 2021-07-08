package com.balloon.ui.components.fragments.userProfile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.balloon.R;
import com.balloon.databinding.DialogImageViewBinding;
import com.balloon.databinding.DialogSuccessfullyBinding;
import com.balloon.databinding.FragmentUserProfileBinding;
import com.balloon.network.ApiResponse;
import com.balloon.pojo.ProfileData;
import com.balloon.ui.base.BaseFragment;
import com.balloon.ui.components.activities.home.HomeActivity;
import com.balloon.ui.components.adapters.UserPhotosAdapter;
import com.balloon.utils.Constants;
import com.balloon.utils.ProgressDialog;
import com.balloon.utils.Utility;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;


public class UserProfileFragment extends BaseFragment {
    private static final String TAG = UserProfileFragment.class.getName();
    private FragmentUserProfileBinding binding;
    private UserProfileViewModel viewModel;
    private View.OnClickListener onClickListener = null;
    private HomeActivity homeActivity;
    private ArrayList<ProfileData.Data.Userinfo.ImagesItem> userPhotoData;
    private UserPhotosAdapter userPhotosAdapter;
    private Bundle mBundle;
    private String userId = "";
    // Animation
    Animation animMoveToTop;


    @Override
    protected ViewDataBinding setBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_profile, container, false);
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
        userPhotoData = new ArrayList<>();
        getBundleData();
        // load the animation
        animMoveToTop = AnimationUtils.loadAnimation(mActivity, R.anim.move);
        //animMoveToTop.setStartOffset(500);
    }

    private void getBundleData() {
        mBundle = this.getArguments();
        if (mBundle != null) {
            userId = mBundle.getString("userId");
            getProfileApi(userId);
        }
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

        if (userinfo.getImages() != null && !userinfo.getImages().isEmpty()) {
            binding.rvPhotos.setVisibility(View.VISIBLE);
            binding.layoutError1.rlerror.setVisibility(View.GONE);
            userPhotoData.clear();
            userPhotoData.addAll(userinfo.getImages());
            if (userPhotoData.size() != 0) {
                userPhotosAdapter = new UserPhotosAdapter(mActivity, onClickListener, userPhotoData);
                binding.rvPhotos.setAdapter(userPhotosAdapter);
            }
        } else {
            binding.rvPhotos.setVisibility(View.GONE);
            binding.layoutError1.rlerror.setVisibility(View.VISIBLE);
        }
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
        viewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);
        viewModel.responseLiveData.observe(this, new Observer<ApiResponse<ProfileData>>() {
            @Override
            public void onChanged(ApiResponse<ProfileData> it) {
                handleResult(it);
            }
        });
    }


    @Override
    protected void setListeners() {
        binding.ivBack.setOnClickListener(this);
        binding.btnSettings.setOnClickListener(this);
        binding.btnMyBio.setOnClickListener(this);
        binding.btnPhoto.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                homeActivity.onBackPressed();
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
            case R.id.rowUserPhotoItem:
                int position = (int) view.getTag();
                userPhotosAdapter.selectedPos = position;
                showImageViewDialog(Constants.BASE_IMG_URL+userPhotoData.get(position).getImage());
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


    private void getProfileApi(String userId) {
        if (sessionManager.getUSER_ID() != null && !sessionManager.getUSER_ID().isEmpty()) {

            HashMap<String, String> reqData = new HashMap<>();
            reqData.put("userId", userId);

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
        super.onDestroy();
    }
}