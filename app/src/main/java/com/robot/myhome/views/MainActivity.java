package com.robot.myhome.views;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.robot.myhome.R;
import com.robot.myhome.Utils.PermissionRequester;

public class MainActivity extends BaseActivity
{
    private final int REQUEST_CODE_PERMISSION = 100;
    private PermissionRequester permissionRequester;

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
