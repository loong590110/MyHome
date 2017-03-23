package com.robot.myhome.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.robot.myhome.views.MainActivity;

/**
 * Created by zailong shi on 2017/3/22 0022.
 */

public class PackageChangeReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED"))
        {
            String packageName = intent.getDataString();
            Log.i("Test", "---------------" + packageName);
            context.sendBroadcast(new Intent().setAction(MainActivity.class.getName()));
        } else if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED"))
        {
            String packageName = intent.getDataString();
            Log.i("Test", "---------------" + "PACKAGE_REMOVED " + packageName);
            context.sendBroadcast(new Intent().setAction(MainActivity.class.getName()));
        }
    }
}
