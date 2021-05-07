package com.balloon.ui.components.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.balloon.R;
import com.balloon.databinding.DialogConfirmationBinding;
import com.balloon.databinding.DialogSuccessfullyBinding;
import com.balloon.pojo.BalloonListData;
import com.balloon.ui.base.RecyclerBaseAdapter;
import com.balloon.utils.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class BalloonsAdapter extends RecyclerBaseAdapter {
    private Activity mActivity;
    private View.OnClickListener onClickListener;
    private ArrayList<BalloonListData.Data.BubblesItem> list;
    private Handler handler;
    private Runnable runnable;


    public BalloonsAdapter(AppCompatActivity mActivity, View.OnClickListener onClickListener, ArrayList<BalloonListData.Data.BubblesItem> list
                          , Handler handler, Runnable runnable) {
        this.mActivity = mActivity;
        this.onClickListener = onClickListener;
        this.list = list;
        this.handler = handler;
        this.runnable = runnable;
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.row_balloons;
    }

    @Override
    public Object getViewModel(int position) {
        return list.get(position);
    }

    @Override
    protected void putViewDataBinding(ViewDataBinding viewDataBinding, int position) {
        View view = viewDataBinding.getRoot();
        AbsoluteLayout rootHeader = view.findViewById(R.id.rowBalloonItem);
        rootHeader.setTag(position);
        rootHeader.setOnClickListener(onClickListener);
        RelativeLayout rlmain = view.findViewById(R.id.rlmain);
        ImageView siListImage = view.findViewById(R.id.siListImage);


        AbsoluteLayout.LayoutParams absParams = (AbsoluteLayout.LayoutParams)rlmain.getLayoutParams();

        Picasso.get().load(list.get(position).getImage()).placeholder(R.drawable.logo_balloon).error(R.drawable.logo_balloon)
                .into(siListImage);
        int width = 600;
        int height = 200;
        Random r = new Random();
        absParams.x =  r.nextInt(width);
        absParams.y =  r.nextInt(height);
        rlmain.setLayoutParams(absParams);

        rootHeader.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //return false;
                Log.d("asdfasdf","dsfsdf____");
                handler.removeCallbacks(runnable);
                showDialog();
                return false;
            }
        });

    }

    public void showDialog() {
        final Dialog dialog = new Dialog(mActivity, R.style.Theme_Dialog);
        DialogConfirmationBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.dialog_confirmation, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.setContentView(dialogBinding.getRoot());
        dialogBinding.btnAprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                handler.post(runnable);
            }
        });
        dialogBinding.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                handler.post(runnable);
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}

