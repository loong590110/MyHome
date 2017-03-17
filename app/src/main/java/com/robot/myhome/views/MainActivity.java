package com.robot.myhome.views;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.robot.myhome.R;
import com.robot.myhome.Utils.AppUtils;
import com.robot.myhome.Utils.PermissionRequester;
import com.robot.myhome.been.AppBean;

import java.util.List;

public class MainActivity extends BaseActivity
{
    private final int REQUEST_CODE_PERMISSION = 100;
    private PermissionRequester permissionRequester;
    private List<AppBean> dockApps;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.app3).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, AppsActivity.class));
            }
        });
        dockApps = AppUtils.getInstance().getDockApps(this);
        if (dockApps.size() > 0)
        {
            ((ImageView)findViewById(R.id.app1)).setImageDrawable(dockApps.get(0).getIcon());
            findViewById(R.id.app1).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    startActivity(getPackageManager().getLaunchIntentForPackage(dockApps.get(0).getPackageName()));
                }
            });
        } else
        {
            findViewById(R.id.app1).setVisibility(View.INVISIBLE);
        }
        if (dockApps.size() > 1)
        {
            ((ImageView) findViewById(R.id.app2)).setImageDrawable(dockApps.get(1).getIcon());
            findViewById(R.id.app2).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    startActivity(getPackageManager().getLaunchIntentForPackage(dockApps.get(1).getPackageName()));
                }
            });
        } else
        {
            findViewById(R.id.app2).setVisibility(View.INVISIBLE);
        }
        if (dockApps.size() > 2)
        {
            ((ImageView) findViewById(R.id.app4)).setImageDrawable(dockApps.get(2).getIcon());
            findViewById(R.id.app4).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    startActivity(getPackageManager().getLaunchIntentForPackage(dockApps.get(2).getPackageName()));
                }
            });
        } else
        {
            findViewById(R.id.app4).setVisibility(View.INVISIBLE);
        }
        if (dockApps.size() > 3)
        {
            ((ImageView) findViewById(R.id.app5)).setImageDrawable(dockApps.get(3).getIcon());
            findViewById(R.id.app5).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    startActivity(getPackageManager().getLaunchIntentForPackage(dockApps.get(3).getPackageName()));
                }
            });
        } else
        {
            findViewById(R.id.app5).setVisibility(View.INVISIBLE);
        }
        String[] permissions = PermissionRequester.checkSelfPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_CONTACTS
        });
        if (permissions.length > 0)
        {
            permissionRequester = PermissionRequester.newInstance(this).requestPermissions(permissions,
                    new PermissionRequester.OnPermissionsGrantedCallback()
                    {
                        @Override
                        public void onPermissionsGranted(String[] permissions)
                        {

                        }
                    }, new PermissionRequester.ShouldShowRequestPermissionRationale()
                    {
                        @Override
                        public void shouldShowRequestPermissionRationale(String permission)
                        {
                            finish();
                        }
                    }, REQUEST_CODE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION)
        {
            permissionRequester.onRequestPermissionsResult(grantResults);
        }
    }

    @Override
    public void onBackPressed()
    {

    }

}
