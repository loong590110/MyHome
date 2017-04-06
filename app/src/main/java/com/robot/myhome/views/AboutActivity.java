package com.robot.myhome.views;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebViewClient;

import com.robot.myhome.R;
import com.robot.myhome.databinding.ActivityAboutBinding;
import com.robot.myhome.services.WebServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        binding.txtTitle.setText("Web Page");
        binding.btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        binding.webView.setWebViewClient(new WebViewClient());
        binding.webView.getSettings().setJavaScriptEnabled(true);
        WebServices.getInstance().getAbout(new Callback<String>()
        {
            @Override
            public void onResponse(Call<String> call, Response<String> response)
            {
                binding.webView.loadDataWithBaseURL("", response.body(), "text/html", "UTF-8", null);
                binding.txtTitle.setText("标题：" + binding.webView.getTitle());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t)
            {
                binding.webView.loadData("error 404", "text/html; charset=UTF-8", null);
            }
        });
    }

}
