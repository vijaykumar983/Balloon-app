package com.balloon.ui.components.fragments.home;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.drawerlayout.widget.DrawerLayout;

import com.balloon.R;
import com.balloon.databinding.FragmentHomeBinding;
import com.balloon.ui.base.BaseFragment;
import com.balloon.ui.components.activities.home.HomeActivity;
import com.balloon.ui.components.fragments.selectBalloon.SelectBalloonFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.util.ArrayList;


public class HomeFragment extends BaseFragment {
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private View.OnClickListener onClickListener = null;
    private HomeActivity homeActivity;

    @Override
    protected ViewDataBinding setBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
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
     binding.rlClick.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlClick:
                homeActivity.changeFragment(new SelectBalloonFragment(), true);
                break;
        }
    }
}