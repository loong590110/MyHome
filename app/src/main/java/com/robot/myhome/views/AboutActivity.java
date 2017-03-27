package com.robot.myhome.views;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebViewClient;

import com.robot.myhome.R;
import com.robot.myhome.databinding.ActivityAboutBinding;

/**
 * Created by zailong shi on 2017/3/27 0027.
 */

public class AboutActivity extends BaseActivity
{
    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about);
        binding.webView.setWebViewClient(new WebViewClient());
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.loadData("adfsd", "text/html", "utf-8");
    }
}
