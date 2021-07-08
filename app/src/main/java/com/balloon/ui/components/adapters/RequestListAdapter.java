package com.balloon.ui.components.adapters;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;

import com.balloon.R;
import com.balloon.pojo.ChatUserListData;
import com.balloon.ui.base.RecyclerBaseAdapter;
import com.balloon.utils.UpdateInterface;
import com.balloon.utils.Utility;

import java.util.ArrayList;


public class RequestListAdapter extends RecyclerBaseAdapter {
    private Activity mActivity;
    private View.OnClickListener onClickListener;
    private ArrayList<ChatUserListData.Data.Bubbles.JsonMemberNewItem> list;
    private UpdateInterface updateInterface;
    public int selectedPos = 0;


    public RequestListAdapter(AppCompatActivity mActivity, View.OnClickListener onClickListener, ArrayList<ChatUserListData.Data.Bubbles.JsonMemberNewItem> list,
                              UpdateInterface updateInterface) {
        this.mActivity = mActivity;
        this.onClickListener = onClickListener;
        this.list = list;
        this.updateInterface = updateInterface;
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.row_request_list;
    }

    @Override
    public Object getViewModel(int position) {
        return list.get(position);
    }

    @Override
    protected void putViewDataBinding(ViewDataBinding viewDataBinding, int position) {
        View view = viewDataBinding.getRoot();
        RelativeLayout rootHeader = view.findViewById(R.id.rowRequestListItem);
        TextView btnConfirm = view.findViewById(R.id.btnConfirm);
        TextView btnDelete = view.findViewById(R.id.btnDelete);
        ImageView ivUserProfile = view.findViewById(R.id.ivUserProfile);
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        Utility.loadImage(ivUserProfile, list.get(position).getImage());
        tvUserName.setText(list.get(position).getName()+" want to join you");

       /* 0 - new request
        1 - accepted
        2 - cancel*/
        /* status accept -1
           statuc cancel - 2 */
        if (list.get(position).getStatus().equals("0")) {
            btnConfirm.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnConfirm.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            rootHeader.setTag(position);
            rootHeader.setOnClickListener(onClickListener);
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInterface.updateData(list.get(position).getUserId(), list.get(position).getId(), "1");
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInterface.updateData(list.get(position).getUserId(), list.get(position).getId(), "2");
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}

