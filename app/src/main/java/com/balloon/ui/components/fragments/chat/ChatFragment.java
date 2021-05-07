package com.balloon.ui.components.fragments.chat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.balloon.R;
import com.balloon.databinding.FragmentChatBinding;
import com.balloon.pojo.Messages;
import com.balloon.ui.base.BaseFragment;
import com.balloon.ui.components.activities.home.HomeActivity;
import com.balloon.ui.components.adapters.ChatAdapter;
import com.balloon.utils.GetTimeAgo;
import com.balloon.utils.Utility;
import com.firebase.client.ServerValue;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatFragment extends BaseFragment {
    private static final String TAG = ChatFragment.class.getName();
    private FragmentChatBinding binding;
    private ChatViewModel homeViewModel;
    private View.OnClickListener onClickListener = null;
    private HomeActivity homeActivity;
    private Bundle mBundle;
    private String userId, userName;

    private DatabaseReference mRootRef;

    private FirebaseAuth mAuth;
    private String mCurrentUserId;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private ChatAdapter mAdapter;

    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;


    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";


    @Override
    protected ViewDataBinding setBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        //viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
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
        getBundleData();


        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mAdapter = new ChatAdapter(mActivity, onClickListener, messagesList,sessionManager);
        mLinearLayout = new LinearLayoutManager(mActivity);
        binding.messagesList.setHasFixedSize(true);
        binding.messagesList.setLayoutManager(mLinearLayout);

        binding.messagesList.setAdapter(mAdapter);

        loadMessages();

        binding.tvUserName.setText(userName);

        mRootRef.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                Utility.loadImage(binding.imvUserProfile,image);

                if (online.equals("true")) {
                    binding.tvSeen.setText("Online");
                    binding.ivOnlineStatus.setVisibility(View.VISIBLE);
                } else {

                    GetTimeAgo getTimeAgo = new GetTimeAgo();

                    long lastTime = Long.parseLong(online);

                    String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, mActivity);

                    binding.tvSeen.setText(lastSeenTime);
                    binding.ivOnlineStatus.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(userId)) {

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", com.google.firebase.database.ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUserId + "/" + userId, chatAddMap);
                    chatUserMap.put("Chat/" + userId + "/" + mCurrentUserId, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null) {
                                Log.d("Chat Log", databaseError.getMessage());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        binding.messageSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;
                itemPos = 0;

                //messagesList.clear();

                loadMoreMessages();
            }
        });

    }

    private void getBundleData() {
        mBundle = getArguments();
        if (mBundle != null) {
            userId = mBundle.getString("userid");
            userName = mBundle.getString("username");
        }
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
        binding.ivBack.setOnClickListener(this);
        binding.llMessageArea.sendButton.setOnClickListener(this);
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
        }
    }

    private void loadMoreMessages() {

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(userId);

        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();

                if (!mPrevKey.equals(messageKey)) {

                    messagesList.add(itemPos++, message);

                } else {

                    mPrevKey = mLastKey;

                }

                if (itemPos == 1) {

                    mLastKey = messageKey;
                }


                mAdapter.notifyDataSetChanged();

                binding.messageSwipeLayout.setRefreshing(false);

                mLinearLayout.scrollToPositionWithOffset(10, 0);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadMessages() {
        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(userId);

        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);

                itemPos++;

                if (itemPos == 1) {
                    mLastKey = dataSnapshot.getKey();
                    mPrevKey = mLastKey;
                }

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                binding.messagesList.scrollToPosition(messagesList.size() - 1);

                binding.messageSwipeLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage() {

        String message = binding.llMessageArea.messageArea.getText().toString();

        String currentUserRef = "messages/" + mCurrentUserId + "/" + userId;
        String chatUserRef = "messages/" + userId + "/" + mCurrentUserId;

        DatabaseReference userMessagePush = mRootRef.child("messages").child(mCurrentUserId).child(userId).push();

        String pushId = userMessagePush.getKey();

        if (!TextUtils.isEmpty(message)) {
            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(currentUserRef + "/" + pushId, messageMap);
            messageUserMap.put(chatUserRef + "/" + pushId, messageMap);

            binding.llMessageArea.messageArea.setText("");

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e(TAG, "database error - " + databaseError.getMessage());
                        Log.e(TAG, "database reference - " + databaseReference.toString());
                    }
                }
            });
        }
    }


}