package com.balloon.ui.components.activities.staticPage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.databinding.DataBindingUtil;

import com.balloon.R;
import com.balloon.databinding.ActivityStaticPageBinding;
import com.balloon.ui.base.BaseBindingActivity;
import com.balloon.utils.ProgressDialog;

public class StaticPageActivity extends BaseBindingActivity {
    private static final String TAG = StaticPageActivity.class.getName();
    private ActivityStaticPageBinding binding;
    private Bundle mBundle;
    private String title = "",url = "";


    @Override
    protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_static_page);
    }

    @Override
    protected void createActivityObject(Bundle savedInstanceState) {
        mActivity = this;
    }

    @Override
    protected void initializeObject() {
        getIntentData();
    }

    private void getIntentData() {
        mBundle = getIntent().getBundleExtra("bundle");
        if (mBundle != null) {
            title = mBundle.getString("title");
            url = mBundle.getString("url");

            binding.appBar.tvTitle.setText(title);

            setWebView(url);
        }
    }

    private void setWebView(String url) {
        ProgressDialog.showProgressDialog(mActivity);
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setDomStorageEnabled(true);
        binding.webView.setWebViewClient(new MyWebViewClient());


        binding.webView.loadUrl(url);
    }

    private static class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url); // load the url
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            ProgressDialog.hideProgressDialog();
        }
    }


    @Override
    protected void setListeners() {
        binding.appBar.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                finish();
                break;
        }
    }

    public static void startActivity(Activity activity, Bundle bundle, boolean isClear) {
        Intent intent = new Intent(activity, StaticPageActivity.class);
        if (bundle != null) intent.putExtra("bundle", bundle);
        if (isClear)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }


}
