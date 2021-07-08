package com.balloon.ui.components.activities.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import com.balloon.R;
import com.balloon.databinding.ActivityHomeBinding;
import com.balloon.ui.base.BaseBindingActivity;
import com.balloon.ui.components.fragments.chat.ChatFragment;
import com.balloon.ui.components.fragments.home.HomeFragment;
import com.balloon.ui.components.fragments.profile.ProfileFragment;
import com.balloon.ui.components.fragments.sendBalloon.SendBalloonFragment;
import com.balloon.ui.components.fragments.userList.UserListFragment;
import com.balloon.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends BaseBindingActivity {
    private ActivityHomeBinding binding;
    private View.OnClickListener onClickListener = null;
    private String chatId = "", userId = "", userName = "", userImg = "", userFirebaseId = "", blockStatus = "",
            check = "", blockBy = "", deviceToken = "", notificationType = "";

    @Override
    protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        onClickListener = this;
        onNewIntent(getIntent());
    }

    @Override
    protected void createActivityObject(Bundle savedInstanceState) {
        mActivity = this;
    }


    @Override
    protected void initializeObject() {
        if (sessionManager.getSelectBalloon()) {
            replaceFragment(new SendBalloonFragment(), null);
        } else {
            replaceFragment(new HomeFragment(), null);
            if(Constants.notificationTest)
            {
                replaceFragment(new UserListFragment(), null);
                Constants.notificationTest = false;
            }

            /*if (notificationType.equals("MsgNotification")) {
               *//* Bundle bundle = new Bundle();
                bundle.putString("firebaseId", userFirebaseId);
                bundle.putString("userId", userId);
                bundle.putString("chatId", chatId);
                bundle.putString("userName", userName);
                bundle.putString("userImg", userImg);
                bundle.putString("blockStatus", blockStatus);
                bundle.putString("blockBy", blockBy);
                bundle.putString("deviceToken", deviceToken);*//*
                replaceFragment(new HomeFragment(), null);
                replaceFragment(new UserListFragment(), null);
            } else {
                replaceFragment(new HomeFragment(), null);
            }*/
        }
    }

    @Override
    protected void setListeners() {

        binding.layoutMain.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHome:
                        replaceFragment(new HomeFragment(), null);
                        return true;
                    case R.id.menuProfile:
                        Bundle bundle = new Bundle();
                        bundle.putString("userId", sessionManager.getUSER_ID());
                        replaceFragment(new ProfileFragment(), null);
                        return true;
                    case R.id.menuMessage:
                        replaceFragment(new UserListFragment(), null);
                        return true;
                }
                return false;
            }
        });

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
           /* case R.id.imvClose:
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                break;*/
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("firebaseId")) {
                // extract the extra-data in the Notification
                /*userFirebaseId = extras.getString("firebaseId");
                userId = extras.getString("userId");
                chatId = extras.getString("chatId");
                userName = extras.getString("userName");
                userImg = extras.getString("userImg");
                blockStatus = extras.getString("blockStatus");
                blockBy = extras.getString("blockBy");
                deviceToken = extras.getString("deviceToken");*/
                //notificationType = extras.getString("notification");
            }
        }
    }


    public static void startActivity(Activity activity, Bundle bundle, boolean isClear) {
        Intent intent = new Intent(activity, HomeActivity.class);
        if (bundle != null) intent.putExtra("bundle", bundle);
        if (isClear)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            int fragments = getSupportFragmentManager().getBackStackEntryCount();
            if (fragments == 1) {
                finish();
            } else if (fragments == 2) {
                //replaceFragment(new HomeFragment(), null);
            }
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
