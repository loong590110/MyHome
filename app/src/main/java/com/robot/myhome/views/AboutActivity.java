package com.robot.myhome.views;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebViewClient;

import com.robot.myhome.R;
import com.robot.myhome.databinding.ActivityAboutBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;

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
        binding.webView.setWebViewClient(new WebViewClient());
        binding.webView.getSettings().setJavaScriptEnabled(true);
        new Retrofit.Builder()
                .baseUrl("http://code.csdn.net/snippets/u1P4y9m0M6D072y6p8B0/master/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(AboutService.class)
                .getAbout()
                .enqueue(new Callback<String>()
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

    private interface AboutService
    {
        @GET("about.html/raw")
        Call<String> getAbout();
    }
}
