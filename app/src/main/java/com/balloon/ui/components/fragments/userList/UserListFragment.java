package com.balloon.ui.components.fragments.userList;

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
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.balloon.R;
import com.balloon.databinding.DialogSuccessfullyBinding;
import com.balloon.databinding.FragmentUserListBinding;
import com.balloon.network.ApiResponse;
import com.balloon.pojo.AcceptRejectData;
import com.balloon.pojo.ChatUserListData;
import com.balloon.ui.base.BaseFragment;
import com.balloon.ui.components.activities.home.HomeActivity;
import com.balloon.ui.components.adapters.RequestListAdapter;
import com.balloon.ui.components.adapters.UserListAdapter;
import com.balloon.ui.components.fragments.chat.ChatFragment;
import com.balloon.utils.Constants;
import com.balloon.utils.ProgressDialog;
import com.balloon.utils.UpdateInterface;
import com.balloon.utils.Utility;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;


public class UserListFragment extends BaseFragment implements UpdateInterface {
    private static final String TAG = UserListFragment.class.getName();
    private FragmentUserListBinding binding;
    private UserListViewModel viewModel;
    private View.OnClickListener onClickListener = null;
    private UpdateInterface updateInterface = null;
    private HomeActivity homeActivity;
    private UserListAdapter userListAdapter;
    private RequestListAdapter requestListAdapter;
    private ArrayList<ChatUserListData.Data.Bubbles.ChatUserItem> userlistData;
    private ArrayList<ChatUserListData.Data.Bubbles.JsonMemberNewItem> reqlistData;


    @Override
    protected ViewDataBinding setBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_list, container, false);
        onClickListener = this;
        updateInterface = this;
        binding.setLifecycleOwner(this);
        return binding;
    }

    @Override
    protected void createActivityObject() {
        mActivity = (AppCompatActivity) getActivity();
    }

    @Override
    protected void initializeObject() {
        binding.appBar.tvTitle.setText("All Users");
        userlistData = new ArrayList<>();
        reqlistData = new ArrayList<>();

        getUserList();

        binding.swipeToRefreshChat.setColorSchemeResources(R.color.color_1786B3);
        binding.swipeToRefreshRequest.setColorSchemeResources(R.color.color_1786B3);
        binding.swipeToRefreshChat.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserList();
                binding.swipeToRefreshChat.setRefreshing(false);
            }
        });
        binding.swipeToRefreshRequest.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserList();
                binding.swipeToRefreshRequest.setRefreshing(false);
            }
        });
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
        viewModel = new ViewModelProvider(this).get(UserListViewModel.class);
        viewModel.responseLiveData.observe(this, new Observer<ApiResponse<ChatUserListData>>() {
            @Override
            public void onChanged(ApiResponse<ChatUserListData> it) {
                handleResult(it);
            }
        });
        viewModel.responseLiveAcceptRejData.observe(this, new Observer<ApiResponse<AcceptRejectData>>() {
            @Override
            public void onChanged(ApiResponse<AcceptRejectData> it) {
                handleAcceptRejResult(it);
            }
        });
    }


    @Override
    protected void setListeners() {
        binding.appBar.ivBack.setOnClickListener(this);
        binding.tvChat.setOnClickListener(this);
        binding.tvJoinReq.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                homeActivity.onBackPressed();
                break;
            case R.id.rowUserListItem:
                int position = (int) view.getTag();
                userListAdapter.selectedPos = position;
                Bundle bundle = new Bundle();
                bundle.putString("firebaseId", userlistData.get(position).getFirebaseId());
                bundle.putString("userId", userlistData.get(position).getUserId());
                bundle.putString("chatId", userlistData.get(position).getId());
                bundle.putString("userName", userlistData.get(position).getName());
                bundle.putString("userImg", userlistData.get(position).getImage());
                bundle.putString("blockStatus", userlistData.get(position).getIsBlock());
                bundle.putString("blockBy", userlistData.get(position).getBlockStatus());
                bundle.putString("deviceToken", userlistData.get(position).getDeviceId());

                homeActivity.changeFragment(new ChatFragment(), true, bundle);
                break;
            case R.id.tvChat:
                setBackground(binding.tvChat, binding.tvJoinReq);
                binding.swipeToRefreshChat.setVisibility(View.VISIBLE);
                binding.swipeToRefreshRequest.setVisibility(View.GONE);
                break;
            case R.id.tvJoinReq:
                setBackground(binding.tvJoinReq, binding.tvChat);
                binding.swipeToRefreshRequest.setVisibility(View.VISIBLE);
                binding.swipeToRefreshChat.setVisibility(View.GONE);
                break;

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setBackground(AppCompatTextView txt1, AppCompatTextView txt2) {
        txt1.setTextColor(mActivity.getResources().getColor(R.color.colorWhite));
        txt1.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.bg_corner_rounded_btn_8dp));
        txt2.setTextColor(mActivity.getResources().getColor(R.color.colorBlack));
        txt2.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.bg_rounded_white));
    }

    private void getUserList() {
        if (sessionManager.getUSER_ID() != null && !sessionManager.getUSER_ID().isEmpty()) {

            HashMap<String, String> reqData = new HashMap<>();
            reqData.put("userId", sessionManager.getUSER_ID());

            if (Utility.isOnline(mActivity)) {
                Log.e(TAG, "Api parameters - " + reqData.toString());
                viewModel.chatUserListApi(reqData);
            } else {
                showNoInternetDialog();
            }
        }
    }

    private void handleResult(ApiResponse<ChatUserListData> result) {
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
                    binding.layoutError.rlerror.setVisibility(View.GONE);
                    binding.layoutError1.rlerror.setVisibility(View.GONE);

                    if (result.getData().getData().getBubbles().getChatUser() != null && !result.getData().getData().getBubbles().getChatUser().isEmpty()) {
                        binding.rvChatList.setVisibility(View.VISIBLE);
                        binding.layoutError.rlerror.setVisibility(View.GONE);
                        userlistData.clear();
                        userlistData.addAll(result.getData().getData().getBubbles().getChatUser());
                        if (userlistData.size() != 0) {
                            userListAdapter = new UserListAdapter(mActivity, onClickListener, userlistData);
                            binding.rvChatList.setAdapter(userListAdapter);
                        }
                    } else {
                        binding.rvChatList.setVisibility(View.GONE);
                        binding.layoutError.rlerror.setVisibility(View.VISIBLE);
                    }

                    if (result.getData().getData().getBubbles().getJsonMemberNew() != null && !result.getData().getData().getBubbles().getJsonMemberNew().isEmpty()) {
                        binding.rvRequestList.setVisibility(View.VISIBLE);
                        binding.layoutError1.rlerror.setVisibility(View.GONE);
                        reqlistData.clear();
                        reqlistData.addAll(result.getData().getData().getBubbles().getJsonMemberNew());
                        if (reqlistData.size() != 0) {
                            requestListAdapter = new RequestListAdapter(mActivity, onClickListener, reqlistData,updateInterface);
                            binding.rvRequestList.setAdapter(requestListAdapter);
                        }
                    } else {
                        binding.rvRequestList.setVisibility(View.GONE);
                        binding.layoutError1.rlerror.setVisibility(View.VISIBLE);
                    }

                } else {
                    binding.layoutError.rlerror.setVisibility(View.VISIBLE);
                    binding.layoutError1.rlerror.setVisibility(View.VISIBLE);
                    //Utility.showToastMessageError(mActivity, result.getData().getMessage());
                }
                break;
        }
    }

    private void acceptRejeApi(String userId, String reqId, String status) {
        if (userId != null && !userId.isEmpty() && reqId != null && !reqId.isEmpty() && status != null && !status.isEmpty()) {

            HashMap<String, String> reqData = new HashMap<>();
            reqData.put("userId", userId);
            reqData.put("rquestId", reqId);
            reqData.put("status", status);

            if (Utility.isOnline(mActivity)) {
                Log.e(TAG, "Api parameters - " + reqData.toString());
                viewModel.acceptRejectApi(reqData);
            } else {
                showNoInternetDialog();
            }
        }
    }

    private void handleAcceptRejResult(ApiResponse<AcceptRejectData> result) {
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

                    getUserList();

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

    @Override
    public void onDestroy() {
        viewModel.disposeSubscriber();
        ProgressDialog.hideProgressDialog();
        super.onDestroy();
    }


    @Override
    public void updateData(String userId, String reqId, String status) {
        acceptRejeApi(userId, reqId, status);
    }
}