package com.robot.myhome;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zailong shi on 2017/3/1 0001.
 */

public class PermissionRequester
{
    private Activity context;
    private List<String> permissions;
    private OnPermissionsGrantedCallback grantedCallback;
    private ShouldShowRequestPermissionRationale callback;
    private int requestCode;
    private String currentPermission;

    private PermissionRequester(Activity context)
    {
        this.context = context;
        permissions = new ArrayList<>();
    }

    public static PermissionRequester newInstance(Activity context)
    {
        return new PermissionRequester(context);
    }

    public static String[] checkSelfPermissions(Context context, String[] permissions)
    {
        List<String> denyPermissions = new ArrayList<>();
        if (permissions != null && permissions.length > 0)
        {
            for (String permission : permissions)
            {
                try
                {
                    if (ContextCompat.checkSelfPermission(context, permission)
                            != PackageManager.PERMISSION_GRANTED)
                    {
                        denyPermissions.add(permission);
                    }
                } catch (Exception e)
                {
                }
            }
        }
        return denyPermissions.toArray(new String[]{});
    }

    public PermissionRequester requestPermissions(String[] permissions,
                                                  OnPermissionsGrantedCallback grantedCallback,
                                                  ShouldShowRequestPermissionRationale callback,
                                                  int requestCode)
    {
        if (context != null)
        {
            this.grantedCallback = grantedCallback;
            this.callback = callback;
            this.requestCode = requestCode;
            if (permissions != null)
            {
                this.permissions.clear();
                for (String permission : permissions)
                {
                    this.permissions.add(permission);
                }
            }
            if (this.permissions.size() > 0)
            {
                requestPermission(this.permissions.get(0));
            }
        }
        return this;
    }

    private void requestPermission(String permission)
    {
        if (context != null)
        {
            currentPermission = permission;
            try
            {
                if (ContextCompat.checkSelfPermission(context, currentPermission)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        ActivityCompat.requestPermissions(context,
                                new String[]{currentPermission},
                                requestCode);
                    } else
                    {
                        grantedCallback.onPermissionsGranted(permissions.toArray(new String[]{}));
                    }
                } else
                {
                    continueOrFinishRequestPermission();
                }
            } catch (Exception e)
            {
                grantedCallback.onPermissionsGranted(permissions.toArray(new String[]{}));
            }
        }
    }

    private void continueOrFinishRequestPermission()
    {
        permissions.remove(currentPermission);
        if (permissions.size() > 0)
        {
            requestPermission(permissions.get(0));
        } else
        {
            grantedCallback.onPermissionsGranted(permissions.toArray(new String[]{}));
        }
    }

    public void onRequestPermissionsResult(int[] grantResults)
    {
        if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            continueOrFinishRequestPermission();
        } else
        {
            callback.shouldShowRequestPermissionRationale(currentPermission);
        }
    }

    public interface OnPermissionsGrantedCallback
    {
        void onPermissionsGranted(String[] permissions);
    }

    public interface ShouldShowRequestPermissionRationale
    {
        void shouldShowRequestPermissionRationale(String permission);
    }
}
