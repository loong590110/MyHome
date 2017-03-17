package com.robot.myhome.been;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by zailong shi on 2017/3/17 0017.
 */

public class AppBean implements Serializable
{
    private Drawable icon;
    private String label;
    private String packageName;

    public Drawable getIcon()
    {
        return icon;
    }

    public void setIcon(Drawable icon)
    {
        this.icon = icon;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }
}
