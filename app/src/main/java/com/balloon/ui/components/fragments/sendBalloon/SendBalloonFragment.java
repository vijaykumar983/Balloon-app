package com.balloon.ui.components.fragments.sendBalloon;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.balloon.R;
import com.balloon.databinding.FragmentSendBalloonBinding;
import com.balloon.ui.base.BaseFragment;
import com.balloon.ui.components.activities.home.HomeActivity;
import com.balloon.ui.components.fragments.selectBalloon.SelectBalloonFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class SendBalloonFragment extends BaseFragment {
    private static final String TAG = SendBalloonFragment.class.getName();
    private FragmentSendBalloonBinding binding;
    private View.OnClickListener onClickListener = null;
    private HomeActivity homeActivity;


    @Override
    protected ViewDataBinding setBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_send_balloon, container, false);
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
                homeActivity.changeFragment(new SelectBalloonFragment(),true);
                break;
        }
    }

}