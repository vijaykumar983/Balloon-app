package com.balloon.ui.components.fragments.selectBalloon;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.balloon.R;
import com.balloon.databinding.DialogSuccessfullyBinding;
import com.balloon.databinding.FragmentSelectBalloonBinding;
import com.balloon.network.ApiResponse;
import com.balloon.pojo.BalloonListData;
import com.balloon.pojo.CategoryData;
import com.balloon.pojo.SendBalloonData;
import com.balloon.ui.base.BaseFragment;
import com.balloon.ui.components.activities.home.HomeActivity;
import com.balloon.ui.components.adapters.BalloonsAdapter;
import com.balloon.ui.components.fragments.balloons.BalloonsFragment;
import com.balloon.utils.Constants;
import com.balloon.utils.ProgressDialog;
import com.balloon.utils.Utility;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;


public class SelectBalloonFragment extends BaseFragment {
    private static final String TAG = SelectBalloonFragment.class.getName();
    private FragmentSelectBalloonBinding binding;
    private SelectBalloonViewModel viewModel;
    private View.OnClickListener onClickListener = null;
    private HomeActivity homeActivity;
    private ArrayList<CategoryData.Data.CategoryItem> categoryData;
    private ArrayList<BalloonListData.Data.BubblesItem> balloonItemData;

   // private static final String[] TEXTS = {"COFFEE", "PIZZA", "BURGER"};
    private int index = 0;
    private String text = "";

    @Override
    protected ViewDataBinding setBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_balloon, container, false);
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
        categoryData = new ArrayList<>();
        balloonItemData = new ArrayList<>();
        binding.textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @SuppressLint("ResourceAsColor")
            @Override
            public View makeView() {
                TextView textView = new TextView(mActivity);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(32f);
                textView.setSelected(true);
                textView.setSingleLine();
                textView.setHorizontallyScrolling(true);
                textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        });
        binding.textSwitcher.setInAnimation(mActivity, android.R.anim.fade_in);
        binding.textSwitcher.setOutAnimation(mActivity, android.R.anim.fade_out);
        categoryApi();
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
        viewModel = new ViewModelProvider(this).get(SelectBalloonViewModel.class);
        viewModel.responseLiveData.observe(this, new Observer<ApiResponse<SendBalloonData>>() {
            @Override
            public void onChanged(ApiResponse<SendBalloonData> it) {
                handleResult(it);
            }
        });
        viewModel.responseLiveCategoryData.observe(this, new Observer<ApiResponse<CategoryData>>() {
            @Override
            public void onChanged(ApiResponse<CategoryData> it) {
                handleCategoryResult(it);
            }
        });
        /*viewModel.responseLiveBalloonListData.observe(this, new Observer<ApiResponse<BalloonListData>>() {
            @Override
            public void onChanged(ApiResponse<BalloonListData> it) {
                handleBalloonListResult(it);
            }
        });*/
    }


    @Override
    protected void setListeners() {
        binding.rlClick.setOnClickListener(this);
        binding.ivPrevious.setOnClickListener(this);
        binding.ivNext.setOnClickListener(this);
        binding.etItem.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    binding.textSwitcher.setText(s.toString().toUpperCase());
                    text = s.toString().toUpperCase();
                } else {
                    if (categoryData != null && categoryData.size() != 0) {
                        binding.textSwitcher.setText(categoryData.get(index).getTitle().toUpperCase());
                        text = categoryData.get(index).getTitle().toUpperCase();
                    }
                }

            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlClick:
                sendBalloonApi();
                break;
            case R.id.ivPrevious:
                if(categoryData.size()>=2)
                {
                    if (index - 1 >= 0)
                        index = index - 1;
                    else
                        index = 1;
                    text = categoryData.get(index).getTitle();
                    binding.textSwitcher.setText(categoryData.get(index).getTitle());
                    binding.etItem.setText("");
                }
                break;
            case R.id.ivNext:
                if(categoryData.size()>=1)
                {
                    if (index + 1 < categoryData.size())
                        index = index + 1;
                    else
                        index = 0;
                    text = categoryData.get(index).getTitle();
                    binding.textSwitcher.setText(categoryData.get(index).getTitle());
                    binding.etItem.setText("");
                }
                break;
        }
    }

    private void categoryApi() {
        if (sessionManager.getUSER_ID() != null && !sessionManager.getUSER_ID().isEmpty()) {

            HashMap<String, String> reqData = new HashMap<>();
            reqData.put("userId", sessionManager.getUSER_ID());

            if (Utility.isOnline(mActivity)) {
                Log.e(TAG, "Api parameters - " + reqData.toString());
                viewModel.category(reqData);
            } else {
                showNoInternetDialog();
            }
        }
    }

    private void handleCategoryResult(ApiResponse<CategoryData> result) {
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

                    if (result.getData().getData().getCategory() != null && !result.getData().getData().getCategory().isEmpty()) {
                        categoryData.clear();
                        categoryData.addAll(result.getData().getData().getCategory());

                        text = categoryData.get(index).getTitle().toUpperCase();
                        binding.textSwitcher.setText(categoryData.get(index).getTitle().toUpperCase());
                    }

                } else {
                    Utility.showToastMessageError(mActivity, result.getData().getMessage());
                }
                break;
        }
    }

    private void sendBalloonApi() {
        if (sessionManager.getUSER_ID() != null && !sessionManager.getUSER_ID().isEmpty() && text != null && !text.isEmpty()) {
            HashMap<String, String> reqData = new HashMap<>();
            reqData.put("userId", sessionManager.getUSER_ID());
            reqData.put("text", text);

            if (Utility.isOnline(mActivity)) {
                Log.e(TAG, "Api parameters - " + reqData.toString());
                viewModel.sendBalloon(reqData);
            } else {
                showNoInternetDialog();
            }
        }
    }

    private void handleResult(ApiResponse<SendBalloonData> result) {
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

                    //getBalloonListApi();
                    homeActivity.changeFragment(new BalloonsFragment(), true);

                } else {
                    Utility.showToastMessageError(mActivity, result.getData().getMessage());
                }
                break;
        }
    }

  /*  private void getBalloonListApi() {
        if (sessionManager.getUSER_ID() !=null && !sessionManager.getUSER_ID().isEmpty()) {

            HashMap<String, String> reqData = new HashMap<>();
            reqData.put("userId", sessionManager.getUSER_ID());

            if (Utility.isOnline(mActivity)) {
                Log.e(TAG, "Api parameters - " + reqData.toString());
                viewModel.balloonList(reqData);
            } else {
                showNoInternetDialog();
            }
        }
    }

    private void handleBalloonListResult(ApiResponse<BalloonListData> result) {
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

                    if (result.getData().getData().getBubbles() !=null && !result.getData().getData().getBubbles().isEmpty()) {
                        balloonItemData.clear();
                        balloonItemData.addAll(result.getData().getData().getBubbles());
                        for (int i = 0; i < 20; i++) {
                            //add to the list
                            balloonItemData.addAll(result.getData().getData().getBubbles());
                        }

                        Log.e(TAG,"data - "+new Gson().toJson(balloonItemData));

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("balloonList",balloonItemData);
                        homeActivity.changeFragment(new BalloonsFragment(), true,bundle);
                    }

                } else {
                    Utility.showToastMessageError(mActivity, result.getData().getMessage());
                }
                break;
        }
    }*/

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
    public void onDestroy() {
        viewModel.disposeSubscriber();
        ProgressDialog.hideProgressDialog();
        super.onDestroy();
    }

}