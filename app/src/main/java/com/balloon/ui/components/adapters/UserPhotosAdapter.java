package com.balloon.ui.components.adapters;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.ViewDataBinding;

import com.balloon.R;
import com.balloon.pojo.ProfileData;
import com.balloon.ui.base.RecyclerBaseAdapter;
import com.balloon.utils.Constants;
import com.balloon.utils.Utility;

import java.util.ArrayList;


public class UserPhotosAdapter extends RecyclerBaseAdapter {
    private AppCompatActivity mActivity;
    private View.OnClickListener onClickListener;
    private ArrayList<ProfileData.Data.Userinfo.ImagesItem> list;
    public  int selectedPos = 0;

    public UserPhotosAdapter(AppCompatActivity mActivity, View.OnClickListener onClickListener, ArrayList<ProfileData.Data.Userinfo.ImagesItem> list) {
        this.mActivity = mActivity;
        this.onClickListener = onClickListener;
        this.list = list;
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.row_user_photos;
    }

    @Override
    public Object getViewModel(int position) {
        return list.get(position);
    }

    @Override
    protected void putViewDataBinding(ViewDataBinding viewDataBinding, int position) {
        View view = viewDataBinding.getRoot();
        RelativeLayout rootHeader = view.findViewById(R.id.rowUserPhotoItem);
        rootHeader.setTag(position);
        rootHeader.setOnClickListener(onClickListener);
        ImageView ivPhoto = view.findViewById(R.id.ivPhoto);
        Utility.loadImage(ivPhoto, Constants.BASE_IMG_URL+list.get(position).getImage());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}

