package com.robot.myhome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

public class AppsActivity extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        registerHomeKeyReceiver();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterHomeKeyReceiver();
    }

    private void registerHomeKeyReceiver()
    {
        registerReceiver(mHomeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    private void unregisterHomeKeyReceiver()
    {
        unregisterReceiver(mHomeKeyEventReceiver);
    }

    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver()
    {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM_HOME_KEY_LONG = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
            {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY))
                {
                    //表示按了home键,程序到了后台
                    //Toast.makeText(getApplicationContext(), "home", 1).show();
                    finish();
                } else if (TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG))
                {
                    //表示长按home键,显示最近使用的程序列表
                }
            }
        }
    };
}
