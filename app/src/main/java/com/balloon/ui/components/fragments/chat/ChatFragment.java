package com.balloon.ui.components.fragments.chat;

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
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.balloon.R;
import com.balloon.databinding.DialogInfoBinding;
import com.balloon.databinding.DialogQuestionBinding;
import com.balloon.databinding.DialogSuccessfullyBinding;
import com.balloon.databinding.FragmentChatBinding;
import com.balloon.network.ApiResponse;
import com.balloon.pojo.BlockUnblockData;
import com.balloon.pojo.Messages;
import com.balloon.pojo.QuestionListData;
import com.balloon.pojo.SubmitReviewData;
import com.balloon.ui.base.BaseFragment;
import com.balloon.ui.components.activities.home.HomeActivity;
import com.balloon.ui.components.adapters.ChatAdapter;
import com.balloon.ui.components.fragments.userProfile.UserProfileFragment;
import com.balloon.utils.Constants;
import com.balloon.utils.ProgressDialog;
import com.balloon.utils.Utility;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatFragment extends BaseFragment {
    private static final String TAG = ChatFragment.class.getName();
    private FragmentChatBinding binding;
    private ChatViewModel viewModel;
    private View.OnClickListener onClickListener = null;
    private HomeActivity homeActivity;
    private Bundle mBundle;
    private String chatId = "", userId = "", userName = "", userImg = "", userFirebaseId = "", blockStatus = "",
            check = "", blockBy = "", deviceToken = "";
    private Firebase reference1, reference2;
    private int index = 0;
    private ArrayList<QuestionListData.Data.CategoryItem> questionData;
    private String senderId = "", senderName = "", senderImg = "", requestId = "";

    private final List<Messages> messagesList = new ArrayList<>();
    private ChatAdapter mAdapter;


    @Override
    protected ViewDataBinding setBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
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
        questionData = new ArrayList<>();
        getBundleData();


        Firebase.setAndroidContext(mActivity);
        reference1 = new Firebase("https://balloon-2f5cc-default-rtdb.firebaseio.com/Messages/" + sessionManager.getFIREBASE_ID() + "_" + userFirebaseId);
        reference2 = new Firebase("https://balloon-2f5cc-default-rtdb.firebaseio.com/Messages/" + userFirebaseId + "_" + sessionManager.getFIREBASE_ID());


        mAdapter = new ChatAdapter(mActivity, onClickListener, messagesList, sessionManager, userImg, userId, homeActivity);
        binding.rvMessages.setHasFixedSize(true);
        binding.rvMessages.setAdapter(mAdapter);

        loadMessages();

    }

    private void getBundleData() {
        mBundle = getArguments();
        if (mBundle != null) {
            chatId = mBundle.getString("chatId");
            userId = mBundle.getString("userId");
            userName = mBundle.getString("userName");
            userFirebaseId = mBundle.getString("firebaseId");
            userImg = mBundle.getString("userImg");
            blockStatus = mBundle.getString("blockStatus");
            blockBy = mBundle.getString("blockBy");
            deviceToken = mBundle.getString("deviceToken");

            binding.tvUserName.setText(userName);
            Utility.loadImage(binding.imvUserProfile, userImg);

            getQuestionListApi();
            if (blockStatus.equals("1")) {
                showBlockUnblockDailog();
            }
        }
    }

    private void showBlockUnblockDailog() {
        final Dialog dialog = new Dialog(mActivity, R.style.Theme_Dialog);
        DialogInfoBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.dialog_info, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setContentView(dialogBinding.getRoot());
        dialogBinding.tvUserName.setText(userName);
        Utility.loadImage(dialogBinding.ivProfile, userImg);
        dialogBinding.btnBlock.setText("Cancel");
        dialogBinding.btnDelete.setText("Unblock");
        dialogBinding.tvUserName.setText("Are you sure, you want to Unblock?");
        dialogBinding.btnDelete.setVisibility(View.VISIBLE);
        dialogBinding.btnBlock.setVisibility(View.VISIBLE);
        if (blockBy != null && !blockBy.equals("")) {
            if (blockBy.equals("1")) {
                dialogBinding.btnDelete.setVisibility(View.VISIBLE);
                dialogBinding.btnBlock.setVisibility(View.VISIBLE);
                dialogBinding.tvUserName.setText("Are you sure, you want to Unblock?");
            }
            if (blockBy.equals("2")) {
                dialogBinding.btnDelete.setVisibility(View.GONE);
                dialogBinding.btnBlock.setVisibility(View.GONE);
                dialogBinding.tvUserName.setText("This user blocked you");
            }
        }
        dialogBinding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                homeActivity.onBackPressed();
            }
        });
        dialogBinding.btnBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                homeActivity.onBackPressed();
            }
        });
        dialogBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                check = "0";
                blockDeleteApi("0"); // 0 for unblock
            }
        });
        dialog.show();
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
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        viewModel.responseLiveQuestionListData.observe(this, new Observer<ApiResponse<QuestionListData>>() {
            @Override
            public void onChanged(ApiResponse<QuestionListData> it) {
                handleQuestionListResult(it);
            }
        });
        viewModel.responseLiveSubmitReviewData.observe(this, new Observer<ApiResponse<SubmitReviewData>>() {
            @Override
            public void onChanged(ApiResponse<SubmitReviewData> it) {
                handleSubmitResult(it);
            }
        });
        viewModel.responseLiveBlockUnblockData.observe(this, new Observer<ApiResponse<BlockUnblockData>>() {
            @Override
            public void onChanged(ApiResponse<BlockUnblockData> it) {
                handleBlockUnblockResult(it);
            }
        });
    }


    @Override
    protected void setListeners() {
        binding.ivBack.setOnClickListener(this);
        binding.llMessageArea.sendButton.setOnClickListener(this);
        binding.rlProfile.setOnClickListener(this);
        binding.ivInfo.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                homeActivity.onBackPressed();
                break;
            case R.id.sendButton:
                sendMessage();
                break;
            case R.id.rlProfile:
                if (userId != null && !userId.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", userId);
                    homeActivity.changeFragment(new UserProfileFragment(), true, bundle);
                }
                break;
            case R.id.ivInfo:
                if (userName != null && !userName.isEmpty() && userImg != null && !userImg.isEmpty()) {
                    showInfoDialog();
                }
                break;
        }
    }

    private void showInfoDialog() {
        final Dialog dialog = new Dialog(mActivity, R.style.Theme_Dialog);
        DialogInfoBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.dialog_info, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setContentView(dialogBinding.getRoot());
        dialogBinding.tvUserName.setText(userName);
        Utility.loadImage(dialogBinding.ivProfile, userImg);
        dialogBinding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialogBinding.btnBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                check = "1";
                blockDeleteApi("1"); // 1 for block
            }
        });
        dialogBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                check = "2";
                blockDeleteApi("2"); // 2 for delete
            }
        });
        dialog.show();
    }

    private void blockDeleteApi(String status) {
        if (userId != null && !userId.isEmpty()) {

            HashMap<String, String> reqData = new HashMap<>();
            reqData.put("status", status);
            reqData.put("chatId", chatId);
            reqData.put("userId", sessionManager.getUSER_ID());

            if (Utility.isOnline(mActivity)) {
                Log.e(TAG, "Api parameters - " + reqData.toString());
                viewModel.blockDeleteApi(reqData);
            } else {
                showNoInternetDialog();
            }
        }
    }

    private void handleBlockUnblockResult(ApiResponse<BlockUnblockData> result) {
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

                    if (check.equals("1")) {
                        homeActivity.onBackPressed();
                    } else if (check.equals("2")) {
                        homeActivity.onBackPressed();
                        reference1.removeValue();
                        reference2.removeValue();
                    } else {

                    }

                } else {
                    Utility.showToastMessageError(mActivity, result.getData().getMessage());
                }
                break;
        }
    }


    private void loadMessages() {
        //reference1.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);
        reference1.addChildEventListener(new com.firebase.client.ChildEventListener() {
            @Override
            public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();
                binding.rvMessages.scrollToPosition(messagesList.size() - 1);
                // binding.messageSwipeLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.firebase.client.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void sendMessage() {
        String messageText = binding.llMessageArea.messageArea.getText().toString();

        if (!messageText.equals("")) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("message", messageText);
            map.put("type", "text");
            map.put("user", sessionManager.getFULL_NAME());
            map.put("time", ServerValue.TIMESTAMP);
            map.put("from", sessionManager.getFIREBASE_ID());
            reference1.push().setValue(map);
            reference2.push().setValue(map);
            sendFCMPush(messageText);

            //Toast.makeText(mActivity, map.toString(), Toast.LENGTH_SHORT).show();
            binding.llMessageArea.messageArea.setText("");
        }
    }

    private void sendFCMPush(String messageText) {

        final String Legacy_SERVER_KEY = "AAAA7pyNQ8s:APA91bEuTGIL1ucxZI1ibm5wjlhCHEBYij7IQgtp-TVs0hm9QPu-Kz4bdg1RFmelgTsLA4Pczu0wTXH5nEDRmuckr1Nlo_RADLvhuJ4jzbNDTI4Vo_ThGwZ-kiw7MkmtN9_s0o3o7v1U";
        String msg = "this is test message,.,,.,.";
        String title = "my title";
        //  String token = "cQP3CuJ_TvawgVxNuqLeEw:APA91bGa_D9jKpkF-IUuRQg7SViNyJKOVIgavpMr7OsevBlSkIZ5LjbZpfIUsBjjlk7XLHJnxmHFu-PeI-wonGAR5qnu-SvkQjQAWUyuB1gawOP8KwyyCnO0eVxMY_x35eUZZgPK0JJO";
        // String token1="f2SwEZWRgxI:APA91bFwSBGeh01nK90ADAhcVUncZsRzTqXljkmqlY8zClH4E-ZRwqQYgmawZ09BnYU9_QA10wLUlMjpy6v-yPUCSrxZpCepcMucbXPzFtwL4XRyH1j3XBA2Sc-3IV8qOV47H98e4qOD";

        String FCM_API = "https://fcm.googleapis.com/fcm/send";

        JSONObject obj = null;
        JSONObject objData = null;
        JSONObject dataobjData = null;

        try {
            obj = new JSONObject();
            objData = new JSONObject();

            objData.put("body", sessionManager.getFULL_NAME()+" Send a Message "+"\n"+messageText);
            objData.put("title", "Balloon");
            objData.put("sound", "default");
            objData.put("icon",  R.mipmap.ic_launcher); //   icon_name image must be there in drawable
            objData.put("tag", deviceToken);
            objData.put("priority", "high");
           /* objData.put("firebaseId", sessionManager.getFIREBASE_ID());
            objData.put("userId", sessionManager.getUSER_ID());
            objData.put("chatId", );
            objData.put("userName", sessionManager.getFULL_NAME());
            objData.put("userImg", sessionManager.getPROFILE_IMAGE());
            objData.put("blockStatus", );
            objData.put("blockBy", blockBy);
            objData.put("deviceToken", sessionManager.getDEVICE_TOKEN());*/
            //objData.put("notification", "MsgNotification");

            dataobjData = new JSONObject();
            dataobjData.put("text",  sessionManager.getFULL_NAME()+" Send a Message "+"\n"+messageText);
            dataobjData.put("title", "Balloon");
            /*dataobjData.put("firebaseId", userFirebaseId);
            dataobjData.put("userId", userId);
            dataobjData.put("chatId", chatId);
            dataobjData.put("userName", userName);
            dataobjData.put("userImg", userImg);
            dataobjData.put("blockStatus", blockStatus);
            dataobjData.put("blockBy", blockBy);
            dataobjData.put("deviceToken", deviceToken);*/
            //dataobjData.put("notification", "MsgNotification");

            obj.put("to", deviceToken);
            //obj.put("priority", "high");
            obj.put("notification", objData);
            obj.put("data", dataobjData);
            Log.e(TAG, obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, FCM_API, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("!_@@_SUCESS", response + "");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("!_@@_Errors--", error + "");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "key=" + Legacy_SERVER_KEY);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }


    private void getQuestionListApi() {
        if (sessionManager.getUSER_ID() != null && !sessionManager.getUSER_ID().isEmpty()) {

            HashMap<String, String> reqData = new HashMap<>();
            reqData.put("userId", sessionManager.getUSER_ID());
            reqData.put("chatId", chatId);

            if (Utility.isOnline(mActivity)) {
                Log.e(TAG, "Api parameters - " + reqData.toString());
                viewModel.questionListApi(reqData);
            } else {
                showNoInternetDialog();
            }
        }
    }

    private void handleQuestionListResult(ApiResponse<QuestionListData> result) {
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
                        questionData.clear();
                        questionData.addAll(result.getData().getData().getCategory());
                        senderId = String.valueOf(result.getData().getSenderId());
                        senderName = String.valueOf(result.getData().getSenderName());
                        senderImg = String.valueOf(result.getData().getSenderPhoto());
                        requestId = String.valueOf(result.getData().getRequestId());
                        if (result.getData().getIshow() == 1) {
                            showQuestionDailog();
                        }

                    }

                } else {
                    Utility.showToastMessageError(mActivity, result.getData().getMessage());
                }
                break;
        }
    }

    private void showQuestionDailog() {
        final Dialog dialog = new Dialog(mActivity, R.style.Theme_Dialog);
        DialogQuestionBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.dialog_question, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(dialogBinding.getRoot());
        Log.e(TAG, "index value - " + index);
        dialogBinding.tvUserName.setText(senderName);
        Utility.loadPicture(dialogBinding.ivProfile, senderImg);
        dialogBinding.tvMessage.setText(questionData.get(index).getTitle().toUpperCase());

        dialogBinding.btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                index = index + 1;
                if (index < questionData.size()) {
                    showQuestionDailog();
                } else {
                    index = 0;
                }
            }
        });

        dialogBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index < questionData.size()) {
                    dialog.dismiss();
                    if (dialogBinding.myRatingBar.getRating() < 1.0f) {
                        submitReviewApi(senderId, 1.0f, questionData.get(index).getId(), requestId);
                    } else {
                        submitReviewApi(senderId, dialogBinding.myRatingBar.getRating(), questionData.get(index).getId(), requestId);
                    }

                }
            }
        });
        dialog.show();
    }

    private void submitReviewApi(String senderId, float rating, String questionId, String requestId) {
        if (sessionManager.getUSER_ID() != null && !sessionManager.getUSER_ID().isEmpty() && senderId != null && !senderId.isEmpty()
                && questionId != null && !questionId.isEmpty()) {

            HashMap<String, String> reqData = new HashMap<>();
            reqData.put("userId", sessionManager.getUSER_ID());
            reqData.put("senderId", senderId);
            reqData.put("questionId", questionId);
            reqData.put("requestId", requestId);
            reqData.put("rating", String.valueOf(rating));

            if (Utility.isOnline(mActivity)) {
                Log.e(TAG, "Api parameters - " + reqData.toString());
                viewModel.submitReviewApi(reqData);
            } else {
                showNoInternetDialog();
            }
        }
    }

    private void handleSubmitResult(ApiResponse<SubmitReviewData> result) {
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

                    index = index + 1;
                    if (index < questionData.size()) {
                        showQuestionDailog();
                    } else {
                        index = 0;
                    }
                } else if (result.getData().getStatusCode() == 666) {

                    index = index + 1;
                    if (index < questionData.size()) {
                        showQuestionDailog();
                    } else {
                        index = 0;
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

    @Override
    public void onDestroy() {
        viewModel.disposeSubscriber();
        ProgressDialog.hideProgressDialog();
        super.onDestroy();
    }

}