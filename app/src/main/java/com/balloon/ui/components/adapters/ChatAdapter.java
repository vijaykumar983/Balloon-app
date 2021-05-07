package com.balloon.ui.components.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;

import com.balloon.R;
import com.balloon.pojo.Messages;
import com.balloon.ui.base.RecyclerBaseAdapter;
import com.balloon.utils.SessionManager;
import com.balloon.utils.Utility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class ChatAdapter extends RecyclerBaseAdapter {
    private Activity mActivity;
    private View.OnClickListener onClickListener;
    private List<Messages> list;
    private DatabaseReference mUserDatabase;
    private SessionManager sessionManager;


    public ChatAdapter(AppCompatActivity mActivity, View.OnClickListener onClickListener, List<Messages> list, SessionManager sessionManager) {
        this.mActivity = mActivity;
        this.onClickListener = onClickListener;
        this.list = list;
        this.sessionManager = sessionManager;
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.row_chat;
    }

    @Override
    public Object getViewModel(int position) {
        return 0;
    }


    @Override
    protected void putViewDataBinding(ViewDataBinding viewDataBinding, int position) {
        View view = viewDataBinding.getRoot();
        RelativeLayout rootHeader = view.findViewById(R.id.rowChatItem);
        rootHeader.setTag(position);
        rootHeader.setOnClickListener(onClickListener);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        ImageView ivUserProfile = view.findViewById(R.id.ivUserProfile);
        RelativeLayout rlMessage = view.findViewById(R.id.rlMessage);
        RelativeLayout rlTextMessage = view.findViewById(R.id.rlTextMessage);


        Messages c = list.get(position);

        String fromUser = c.getFrom();
        String messageType = c.getType();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUser);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                String name = dataSnapshot.child("name").getValue().toString();
//                String image = dataSnapshot.child("thumb_image").getValue().toString();

                //               holder.displayName.setText(name);

                /*Picasso.with(holder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.profile).into(holder.profileImage);*/
                //           Utility.loadImage(holder.profileImage,image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (fromUser.equals(sessionManager.getFIREBASE_ID())) {
            //current logged in user
            //ivUserProfile.setVisibility(View.GONE);
            rlMessage.setGravity(Gravity.END);
            //rlTextMessage.setBackgroundResource(R.drawable.bubble_in);
            rlTextMessage.setBackgroundResource(R.drawable.my_message);
            tvMessage.setTextColor(mActivity.getResources().getColor(R.color.colorWhite));
            //rlTextMessage.setBackgroundTintList(mActivity.getResources().getColorStateList(R.color.color_1786B3));
        } else {
            //ivUserProfile.setVisibility(View.VISIBLE);
            rlMessage.setGravity(Gravity.START);
            //rlTextMessage.setBackgroundResource(R.drawable.bubble_out);
            rlTextMessage.setBackgroundResource(R.drawable.their_message);
            tvMessage.setTextColor(mActivity.getResources().getColor(R.color.colorBlack));
            //rlTextMessage.setBackgroundTintList(null);
        }
        tvMessage.setText(c.getMessage());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}

