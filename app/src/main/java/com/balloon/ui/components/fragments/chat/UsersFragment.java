package com.balloon.ui.components.fragments.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.balloon.R;
import com.balloon.databinding.FragmentUsersBinding;
import com.balloon.ui.base.BaseFragment;
import com.balloon.ui.components.activities.home.HomeActivity;
import com.balloon.utils.Friends;
import com.balloon.utils.ProgressDialog;
import com.balloon.utils.Utility;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;


public class UsersFragment extends BaseFragment {
    private static final String TAG = UsersFragment.class.getName();
    private FragmentUsersBinding binding;
    private ChatViewModel viewModel;
    private View.OnClickListener onClickListener = null;
    private HomeActivity homeActivity;

    private DatabaseReference mUsersDB;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;


    @Override
    protected ViewDataBinding setBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_users, container, false);
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
        binding.appBar.tvTitle.setText("All Users");

        mAuth = FirebaseAuth.getInstance();

        mCurrentUserId = mAuth.getCurrentUser().getUid();

        Log.e(TAG,"userId - "+mCurrentUserId);

        mUsersDB = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDB.keepSynced(true);

        binding.friendsList.setHasFixedSize(true);
        binding.friendsList.setLayoutManager(new LinearLayoutManager(getContext()));
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
    }


    @Override
    protected void setListeners() {
        binding.appBar.ivBack.setOnClickListener(this);
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


    @Override
    public void onStart() {
        super.onStart();
        ProgressDialog.showProgressDialog(mActivity);
        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class,
                R.layout.row_users,
                FriendsViewHolder.class,
                mUsersDB
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, final Friends friends, int position) {
                ProgressDialog.hideProgressDialog();
                friendsViewHolder.setDate(friends.getDate());

                final String list_user_id = getRef(position).getKey();

                if(!sessionManager.getFIREBASE_ID().equals(list_user_id))
                {
                    friendsViewHolder.mView.findViewById(R.id.rowUsersItem).setVisibility(View.VISIBLE);
                    friendsViewHolder.mView.findViewById(R.id.rowUsersItem).setPadding(0,10,0,10);
                    friendsViewHolder.mView.findViewById(R.id.rowUsersItem).setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    mUsersDB.child(list_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.e(TAG,"data - "+dataSnapshot.toString());

                            if (dataSnapshot.hasChildren()) {
                                final String userName = dataSnapshot.child("name").getValue().toString();
                                String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                                if (dataSnapshot.hasChild("online")) {
                                    String userOnline = dataSnapshot.child("online").getValue().toString();
                                    friendsViewHolder.setUserOnline(userOnline);
                                }


                                friendsViewHolder.setName(userName);

                                friendsViewHolder.setUserImage(userThumb, getContext());

                                friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Bundle bundle = new Bundle();
                                        bundle.putString("userid", list_user_id);
                                        bundle.putString("username", userName);
                                        homeActivity.changeFragment(new ChatFragment(), true, bundle);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    friendsViewHolder.mView.findViewById(R.id.rowUsersItem).setVisibility(View.GONE);
                    friendsViewHolder.mView.findViewById(R.id.rowUsersItem).setPadding(0,0,0,0);
                    friendsViewHolder.mView.findViewById(R.id.rowUsersItem).setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
            }
        };


        binding.friendsList.setAdapter(friendsRecyclerViewAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        View  mView;


        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setDate(String date){
            /*TextView userNameView = (TextView) mView.findViewById(R.id.tvSeen);
            userNameView.setText(date);*/
        }

        public void setName(String name){
            TextView userNameView = (TextView)mView.findViewById(R.id.tvUserName);
            userNameView.setText(name);
        }

        public void setUserImage(String thumbImage, Context ctx){
            CircularImageView userImageView = (CircularImageView)mView.findViewById(R.id.ivUserProfile);
            //Picasso.with(ctx).load(thumbImage).placeholder(R.drawable.profile).into(userImageView);
            Utility.loadImage(userImageView,thumbImage);
        }

        public void setUserOnline(String onlineStatus){

            ImageView userOnlineView =(ImageView)mView.findViewById(R.id.ivOnlineStatus);

            if(onlineStatus.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
            }else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }

        }
    }


}