package com.balloon.ui.components.fragments.balloons;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.balloon.R;
import com.balloon.databinding.DialogSuccessfullyBinding;
import com.balloon.databinding.FragmentBalloonsBinding;
import com.balloon.network.ApiResponse;
import com.balloon.pojo.BalloonListData;
import com.balloon.ui.base.BaseFragment;
import com.balloon.ui.components.activities.home.HomeActivity;
import com.balloon.ui.components.adapters.BalloonsAdapter;
import com.balloon.utils.Constants;
import com.balloon.utils.ProgressDialog;
import com.balloon.utils.ShapesImage;
import com.balloon.utils.Utility;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class BalloonsFragment extends BaseFragment {
    private static final String TAG = BalloonsFragment.class.getName();
    private FragmentBalloonsBinding binding;
    private BalloonsViewModel viewModel;
    private View.OnClickListener onClickListener = null;
    private HomeActivity homeActivity;
    private BalloonsAdapter balloonsAdapter;
    private ArrayList<BalloonListData.Data.BubblesItem> balloonItemData;
    int scrollCount = 0;
    Handler handler;
    Runnable runnable;


    @Override
    protected ViewDataBinding setBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_balloons, container, false);
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
        balloonItemData = new ArrayList<>();
    }


    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = mActivity.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        handler = new Handler();

        getBalloonListApi();
    }


    @Override
    protected void initializeOnCreateObject() {
        homeActivity = (HomeActivity) getActivity();
        viewModel = new ViewModelProvider(this).get(BalloonsViewModel.class);
        viewModel.responseLiveData.observe(this, new Observer<ApiResponse<BalloonListData>>() {
            @Override
            public void onChanged(ApiResponse<BalloonListData> it) {
                handleResult(it);
            }
        });
    }


    @Override
    protected void setListeners() {
        binding.ivBack.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                homeActivity.onBackPressed();
                break;
        }
    }

    private void getBalloonListApi() {
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

    private void handleResult(ApiResponse<BalloonListData> result) {
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
                        for (int i = 0; i < 1000; i++) {
                            //add to the list
                            balloonItemData.addAll(result.getData().getData().getBubbles());
                        }
                        if (balloonItemData.size() != 0) {
                            //balloonsAdapter = new BalloonsAdapter(mActivity, onClickListener, balloonItemData);
                            //binding.rvBalloons.setAdapter(balloonsAdapter);
                            flyBalloon();
                        }
                    }

                } else {
                    Utility.showToastMessageError(mActivity, result.getData().getMessage());
                }
                break;
        }
    }

    private void flyBalloon() {
        //balloonsAdapter = new BalloonsAdapter(mActivity,onClickListener, balloonItemData,handler,runnable);
        //binding.rvBalloons.setAdapter(balloonsAdapter);
        balloonsAdapter = new BalloonsAdapter();
        binding.rvBalloons.setAdapter(balloonsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity) {

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(mActivity) {
                    private static final float SPEED = 2500f;// Change this value (default=25f)

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return SPEED / displayMetrics.densityDpi;
                    }
                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rvBalloons.setLayoutManager(layoutManager);
        binding.rvBalloons.setHasFixedSize(true);
        binding.rvBalloons.setItemViewCacheSize(1000);
        binding.rvBalloons.setDrawingCacheEnabled(true);
        binding.rvBalloons.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        binding.rvBalloons.setAdapter(balloonsAdapter);
        //remove manual scroll
        binding.rvBalloons.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return e.getAction() == MotionEvent.ACTION_MOVE;
            }
            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

        scrollCount = 0;
        //handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("qwerty", scrollCount + "___" + balloonsAdapter.getItemCount());
                binding.rvBalloons.smoothScrollToPosition((scrollCount++));
                if (balloonsAdapter.getItemCount()>5){
                }else{
                    balloonItemData.addAll(balloonItemData);
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
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


    //adapter
    public class BalloonsAdapter extends RecyclerView.Adapter<BalloonsAdapter.MyViewHolder> {

        @Override
        public BalloonsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.row_balloons, parent, false);
            return new BalloonsAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            AbsoluteLayout.LayoutParams absParams = (AbsoluteLayout.LayoutParams)holder.rlmain.getLayoutParams();

            Picasso.get().load(balloonItemData.get(position).getImage()).placeholder(R.drawable.logo_balloon).error(R.drawable.logo_balloon).into(holder.siListImage);
            int width = 600;
            int height = 200;
            Random r = new Random();
            absParams.x =  r.nextInt(width);
            absParams.y =  r.nextInt(height);
            holder.rlmain.setLayoutParams(absParams);

            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //return false;
                    Log.d("asdfasdf","dsfsdf____");
                    handler.removeCallbacks(runnable);
                    showDialog(balloonItemData.get(position).getName(),balloonItemData.get(position).getImage());
                    return false;
                }
            });

        }


        @Override
        public int getItemCount() {
            return balloonItemData.size();
        }

        public long getItemId(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private final RelativeLayout rlmain;
            ShapesImage siListImage;

            public MyViewHolder(View v) {
                super(v);
                this.rlmain = v.findViewById(R.id.rlmain);
                this.siListImage = v.findViewById(R.id.siListImage);
            }
        }
    }

    public void showDialog(String name, String image) {
        final Dialog dialog = new Dialog(mActivity,
                R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView btn_remove = dialog.findViewById(R.id.btn_remove);
        ImageView btn_aprove = dialog.findViewById(R.id.btn_aprove);
        TextView tvmessage = dialog.findViewById(R.id.tvmessage);
        ImageView siListImage = dialog.findViewById(R.id.siListImage);
        tvmessage.setText("Name : "+name);
        Utility.loadImage(siListImage,image);

        btn_aprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                handler.post(runnable);
            }
        });
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                handler.post(runnable);
            }
        });
        dialog.show();
    }

    @Override
    public void onDestroy() {
        viewModel.disposeSubscriber();
        ProgressDialog.hideProgressDialog();
        super.onDestroy();
    }

}