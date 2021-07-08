package com.balloon.ui.components.fragments.home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.balloon.databinding.FragmentHomeBinding;
import com.balloon.network.ApiResponse;
import com.balloon.pojo.BalloonListData;
import com.balloon.pojo.SendRequestData;
import com.balloon.ui.base.BaseFragment;
import com.balloon.ui.components.activities.home.HomeActivity;
import com.balloon.ui.components.fragments.profile.ProfileFragment;
import com.balloon.ui.components.fragments.selectBalloon.SelectBalloonFragment;
import com.balloon.ui.components.fragments.userProfile.UserProfileFragment;
import com.balloon.utils.Constants;
import com.balloon.utils.ProgressDialog;
import com.balloon.utils.ShapesImage;
import com.balloon.utils.Utility;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class HomeFragment extends BaseFragment {
    private static final String TAG = HomeFragment.class.getName();
    private FragmentHomeBinding binding;
    private HomeFragmentViewModel viewModel;
    private View.OnClickListener onClickListener = null;
    private HomeActivity homeActivity;
    private MyBalloonsAdapter myBalloonsAdapter;
    private ArrayList<BalloonListData.Data.BubblesItem> balloonItemData;
    int scrollCount = 0;
    private Handler handler;
    private Runnable runnable;
    //private static String SHOWCASE_ID = "";
    private static final String SHOWCASE_ID = "sequence example5";

    private LinearSmoothScroller smoothScroller;
    private final Handler mHandler = new Handler();
    public boolean isDoubleCliked = false;
    private boolean isKilled = false;


    @Override
    protected ViewDataBinding setBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
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

        doTheAutoRefresh();
        goThroughApp();
    }

    private void goThroughApp() {
        //Random r = new Random();
        //SHOWCASE_ID = String.valueOf(r.nextInt(80 - 65) + 65);

        // sequence example
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ShowcaseConfig config = new ShowcaseConfig();
                config.setDelay(300); // half second between each showcase view

                MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(mActivity, SHOWCASE_ID);

                sequence.setConfig(config);

                sequence.addSequenceItem(homeActivity.findViewById(R.id.menuProfile), "Tell them about yourself",
                        "Share about yourself who you are, what do you do, what you like, anything and put a picture too.", "Next");

                sequence.addSequenceItem(homeActivity.findViewById(R.id.menuHome), "Send a balloon",
                        "Want someone to share a meal with or grab a coffee or even a morning walk, send a balloon.", "Next");

                sequence.addSequenceItem(homeActivity.findViewById(R.id.menuMessage), "Start ballooning",
                        "If you expect someone's request you guys can chat.", "Next");

                sequence.addSequenceItem(binding.view,
                        "Hold a floating balloon to see who it belongs to and what activity they want to do.And if you want to join them send a request by clicking on the activity icon.", "GOT IT");


                sequence.start();
            }
        }, 5000);

    }

    private void doTheAutoRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Write code for your refresh logic
                getBalloonListApi();
                doTheAutoRefresh();
            }
        }, 4 * 60 * 1000); //1 milisecond = 1000
    }


    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = mActivity.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        handler = new Handler();
        isKilled = false;
        getBalloonListApi();
    }


    @Override
    protected void initializeOnCreateObject() {
        homeActivity = (HomeActivity) getActivity();
        viewModel = new ViewModelProvider(this).get(HomeFragmentViewModel.class);
        viewModel.responseLiveData.observe(this, new Observer<ApiResponse<BalloonListData>>() {
            @Override
            public void onChanged(ApiResponse<BalloonListData> it) {
                handleResult(it);
            }
        });
        viewModel.responseLiveSendReqData.observe(this, new Observer<ApiResponse<SendRequestData>>() {
            @Override
            public void onChanged(ApiResponse<SendRequestData> it) {
                handleSendReqResult(it);
            }
        });

    }


    @Override
    protected void setListeners() {
        binding.ivBack.setOnClickListener(this);
        binding.btnAddBalloon.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                homeActivity.onBackPressed();
                break;
            case R.id.btnAddBalloon:
                homeActivity.changeFragment(new SelectBalloonFragment(), true);
                break;
        }
    }


    private void getBalloonListApi() {
        if (sessionManager.getUSER_ID() != null && !sessionManager.getUSER_ID().isEmpty()) {

            HashMap<String, String> reqData = new HashMap<>();
            reqData.put("userId", sessionManager.getUSER_ID());
            reqData.put("let", sessionManager.getLATITUDE());
            reqData.put("lng", sessionManager.getLONGITUDE());

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

                    if (result.getData().getData().getBubbles() != null && !result.getData().getData().getBubbles().isEmpty()) {
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

    private void sendRequestApi(String bubbleId, String id) {
        if (id != null && !id.isEmpty()) {

            HashMap<String, String> reqData = new HashMap<>();
            reqData.put("userId", sessionManager.getUSER_ID());
            reqData.put("senderId", id);
            reqData.put("bubbleId", bubbleId);

            if (Utility.isOnline(mActivity)) {
                Log.e(TAG, "Api parameters - " + reqData.toString());
                viewModel.sendRequestApi(reqData);
            } else {
                showNoInternetDialog();
            }
        }
    }

    private void handleSendReqResult(ApiResponse<SendRequestData> result) {
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
                    showSuccessfullyDialog(result.getData().getMessage());
                } else if (result.getData().getStatusCode() == 666) {
                    showSuccessfullyDialog(result.getData().getMessage());
                } else {
                    Utility.showToastMessageError(mActivity, result.getData().getMessage());
                }
                break;
        }
    }

    private void showSuccessfullyDialog(String message) {
        final Dialog dialog = new Dialog(mActivity, R.style.Theme_Dialog);
        DialogSuccessfullyBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.dialog_successfully, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialogBinding.tvTitle.setText("Successfully");
        dialogBinding.tvMessage.setText(message);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setContentView(dialogBinding.getRoot());
        dialogBinding.btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                handler.post(runnable);
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void flyBalloon() {
        //balloonsAdapter = new BalloonsAdapter(mActivity,onClickListener, balloonItemData,handler,runnable);
        //binding.rvBalloons.setAdapter(balloonsAdapter);

        myBalloonsAdapter = new MyBalloonsAdapter();
        binding.rvBalloons.setAdapter(myBalloonsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity) {

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                smoothScroller = new LinearSmoothScroller(mActivity) {
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
        binding.rvBalloons.setAdapter(myBalloonsAdapter);
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
                if (!isKilled) {
                    Log.d("qwerty", scrollCount + "___" + myBalloonsAdapter.getItemCount());
                    binding.rvBalloons.smoothScrollToPosition((scrollCount++));
                    if (scrollCount > 5) {

                    } else {
                        balloonItemData.addAll(balloonItemData);
                    }
                    handler.postDelayed(this, 1000);
                }
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

    String tag = "1";

    //adapter
    public class MyBalloonsAdapter extends RecyclerView.Adapter<MyBalloonsAdapter.MyViewHolder> {

        @Override
        public MyBalloonsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.row_balloons, parent, false);
            return new MyBalloonsAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyBalloonsAdapter.MyViewHolder holder, final int position) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            //  int height = displayMetrics.heightPixels;
            //  int width = displayMetrics.widthPixels;
            AbsoluteLayout.LayoutParams absParams = (AbsoluteLayout.LayoutParams) holder.rlmain.getLayoutParams();

            if (balloonItemData.get(position).getImage() != null && !balloonItemData.get(position).getImage().equals("")) {
                Picasso.get().load(balloonItemData.get(position).getImage()).placeholder(R.color.white).error(R.color.white).into(holder.siListImage);
            }
            int width = displayMetrics.widthPixels;
            //Log.d("adfasdf",width+"__");
            int height = 200;
            Random r = new Random();
            int abc = width / 2;
            absParams.x = r.nextInt(abc - 50); //
            Log.d("adfasdf", absParams.x + "__");
            absParams.y = r.nextInt(height);

            holder.rlmain.setLayoutParams(absParams);
            holder.llName.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //Toast.makeText(mActivity,"name",Toast.LENGTH_SHORT).show();
                    if (balloonItemData.get(position).getId().equals(sessionManager.getUSER_ID())) {
                        homeActivity.changeFragment(new ProfileFragment(), true);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("userId", balloonItemData.get(position).getId());
                        homeActivity.changeFragment(new UserProfileFragment(), true, bundle);
                    }
                    return false;
                }
            });

            holder.llActivity.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Toast.makeText(mActivity,"activity",Toast.LENGTH_SHORT).show();
                    if (!balloonItemData.get(position).getId().equals(sessionManager.getUSER_ID())) {
                        handler.removeCallbacks(runnable);
                        sendRequestApi(balloonItemData.get(position).getBubbleId(), balloonItemData.get(position).getId());
                    }
                    return false;
                }
            });

            holder.siListImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //return false;
                    Log.d("asdfasdf", tag);
                    //  rvTickerList.stopScroll();
                    /*if (tag.equals("1")) {
                        tag = "2";
                        handler.removeCallbacks(runnable);
                        holder.llActivity.setVisibility(View.VISIBLE);
                        holder.llName.setVisibility(View.VISIBLE);
                        handler.removeCallbacks(runnable);
                        //rvTickerList.stopScroll();
                    } else {
                        handler.post(runnable);
                        holder.llActivity.setVisibility(View.GONE);
                        holder.llName.setVisibility(View.GONE);
                        tag = "1";
                        handler.post(runnable);
                    }*/

                   /* handler.removeCallbacks(runnable);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            handler.post(runnable);
                            Log.e("Test","Delay call");
                        }
                    }, 5000);*/

                    if (isDoubleCliked) {
                        //Actions when double Clicked
                        isDoubleCliked = false;
                        //remove callbacks for Handlers
                        tag = "2";
                        // handler.removeCallbacks(runnable);
                        holder.llActivity.setVisibility(View.VISIBLE);
                        holder.llName.setVisibility(View.VISIBLE);

                        handler.removeCallbacks(runnable);
                        kill();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isKilled = false;
                                handler.post(runnable);
                                //Log.e("Test","Delay call");
                            }
                        }, 5000);
                    } else {
                        isDoubleCliked = true;
                        //handler.post(runnable);
                        holder.llActivity.setVisibility(View.GONE);
                        holder.llName.setVisibility(View.GONE);
                        tag = "1";
                    }
                    return false;
                }
            });
            holder.tvname.setText(balloonItemData.get(position).getName());
            holder.tvactivity.setText(balloonItemData.get(position).getTitle());
            Utility.loadPicture(holder.ivProfile, balloonItemData.get(position).getImage());
            if (balloonItemData.get(position).getCategoryimage() != null && !balloonItemData.get(position).getCategoryimage().equals("")) {
                Utility.loadPicture(holder.ivCategory, balloonItemData.get(position).getCategoryimage());
            }

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
            private final TextView tvactivity;
            private final TextView tvname;
            private final LinearLayout llName;
            private final LinearLayout llActivity;
            private final ImageView ivProfile;
            private final ImageView ivCategory;
            ShapesImage siListImage;

            public MyViewHolder(View v) {
                super(v);
                this.rlmain = v.findViewById(R.id.rlmain);
                this.siListImage = v.findViewById(R.id.siListImage);
                this.tvactivity = v.findViewById(R.id.tvactivity);
                this.tvname = v.findViewById(R.id.tvname);
                this.llName = v.findViewById(R.id.llName);
                this.llActivity = v.findViewById(R.id.llActivity);
                this.ivProfile = v.findViewById(R.id.ivProfile);
                this.ivCategory = v.findViewById(R.id.ivCategory);
            }
        }
    }


    final public void kill() {
        isKilled = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacks(runnable);
            kill();
        }
    }

    @Override
    public void onDestroy() {
        viewModel.disposeSubscriber();
        ProgressDialog.hideProgressDialog();
        if (handler != null) {
            handler.removeCallbacks(runnable);
            kill();
        }
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacks(runnable);
            kill();
        }
    }
}