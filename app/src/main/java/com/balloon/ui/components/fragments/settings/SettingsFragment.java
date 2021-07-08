package com.balloon.ui.components.fragments.settings;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.balloon.R;
import com.balloon.databinding.DialogLogoutBinding;
import com.balloon.databinding.FragmentSettingBinding;
import com.balloon.ui.base.BaseFragment;
import com.balloon.ui.components.activities.home.HomeActivity;
import com.balloon.ui.components.activities.login.LoginActivity;
import com.balloon.ui.components.activities.loginMain.LoginMainActivity;
import com.balloon.ui.components.activities.loginOption.LoginOptionActivity;
import com.balloon.ui.components.activities.staticPage.StaticPageActivity;
import com.balloon.ui.components.fragments.editProfile.EditProfileFragment;
import com.balloon.utils.Constants;
import com.balloon.utils.Utility;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class SettingsFragment extends BaseFragment {
    private static final String TAG = SettingsFragment.class.getName();
    private FragmentSettingBinding binding;
    private View.OnClickListener onClickListener = null;
    private HomeActivity homeActivity;


    @Override
    protected ViewDataBinding setBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
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
        binding.appBar.tvTitle.setText("Settings");
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
        binding.llEditProfile.setOnClickListener(this);
        binding.llPrivacyPolicy.setOnClickListener(this);
        binding.llTermCondition.setOnClickListener(this);
        binding.llLogout.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        Utility.hideKeyboard(mActivity);
        switch (view.getId()) {
            case R.id.ivBack:
                homeActivity.onBackPressed();
                break;
            case R.id.llEditProfile:
                homeActivity.changeFragment(new EditProfileFragment(), true);
                break;
            case R.id.llPrivacyPolicy:
                Bundle bundle = new Bundle();
                bundle.putString("title", "Privacy Policy");
                bundle.putString("url", Constants.PRIVACY_POLICY_URL);
                StaticPageActivity.startActivity(mActivity, bundle, false);
                break;
            case R.id.llTermCondition:
                Bundle bundle1 = new Bundle();
                bundle1.putString("title", "Term and Condition");
                bundle1.putString("url", Constants.TERM_CONDITION_URL);
                StaticPageActivity.startActivity(mActivity, bundle1, false);
                break;
            case R.id.llLogout:
                showLogoutDailog();
                break;
        }
    }

    private void showLogoutDailog() {
        final Dialog dialog = new Dialog(mActivity, R.style.Theme_Dialog);
        DialogLogoutBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.dialog_logout, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(dialogBinding.getRoot());
        dialogBinding.tvTitle.setText("Logout");
        dialogBinding.tvMessage.setText("Are you sure, you want to logout?");
        dialogBinding.btnYes.setText("Yes");
        dialogBinding.btnNo.setText("No");
        dialogBinding.btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialogBinding.btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                sessionManager.logout();
                GoogleSignInOptions gso = new GoogleSignInOptions.
                        Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                        build();
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(mActivity, gso);
                googleSignInClient.signOut();//gmail logout

                LoginManager.getInstance().logOut(); //facebook logout
                AccessToken.setCurrentAccessToken(null);
                LoginMainActivity.startActivity(mActivity, null, true);
                homeActivity.finish();
            }
        });
        dialog.show();
    }

}