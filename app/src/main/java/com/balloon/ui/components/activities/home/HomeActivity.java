package com.balloon.ui.components.activities.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import com.balloon.R;
import com.balloon.databinding.ActivityHomeBinding;
import com.balloon.ui.base.BaseBindingActivity;
import com.balloon.ui.components.fragments.chat.ChatFragment;
import com.balloon.ui.components.fragments.chat.UsersFragment;
import com.balloon.ui.components.fragments.home.HomeFragment;
import com.balloon.ui.components.fragments.profile.ProfileFragment;
import com.balloon.utils.Balloon;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.util.ArrayList;

public class HomeActivity extends BaseBindingActivity {
    private ActivityHomeBinding binding;
    private View.OnClickListener onClickListener = null;


    @Override
    protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        onClickListener = this;
    }

    @Override
    protected void createActivityObject(Bundle savedInstanceState) {
        mActivity = this;
    }


    @Override
    protected void initializeObject() {
        replaceFragment(new HomeFragment(), null);
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
                        replaceFragment(new ProfileFragment(), null);
                        return true;
                    case R.id.menuMessage:
                        replaceFragment(new UsersFragment(), null);
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
