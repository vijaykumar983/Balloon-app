package com.balloon.ui.components.adapters;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;

import com.balloon.R;
import com.balloon.pojo.ChatUserListData;
import com.balloon.ui.base.RecyclerBaseAdapter;
import com.balloon.utils.Utility;

import java.util.ArrayList;


public class UserListAdapter extends RecyclerBaseAdapter {
    private Activity mActivity;
    private View.OnClickListener onClickListener;
    private ArrayList<ChatUserListData.Data.Bubbles.ChatUserItem> list;
    public int selectedPos = 0;


    public UserListAdapter(AppCompatActivity mActivity, View.OnClickListener onClickListener, ArrayList<ChatUserListData.Data.Bubbles.ChatUserItem> list
    ) {
        this.mActivity = mActivity;
        this.onClickListener = onClickListener;
        this.list = list;
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.row_user_list;
    }

    @Override
    public Object getViewModel(int position) {
        return list.get(position);
    }

    @Override
    protected void putViewDataBinding(ViewDataBinding viewDataBinding, int position) {
        View view = viewDataBinding.getRoot();
        RelativeLayout rootHeader = view.findViewById(R.id.rowUserListItem);
        rootHeader.setTag(position);
        rootHeader.setOnClickListener(onClickListener);
        ImageView ivUserProfile = view.findViewById(R.id.ivUserProfile);
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        Utility.loadImage(ivUserProfile, list.get(position).getImage());
        tvUserName.setText(list.get(position).getName());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}

