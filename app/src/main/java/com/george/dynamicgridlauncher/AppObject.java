package com.george.dynamicgridlauncher;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

public class AppObject {
    private String name,packageName;
    private Drawable image;

    long playTime = 0;

    public int catagory = ApplicationInfo.CATEGORY_UNDEFINED;
    public AppObject(String PackageName, String name, Drawable image)
    {
        this.name = name;
        this.packageName = PackageName;
        this.image = image;

    }

    public String getPackageName()
    {
        return this.packageName;
    }
    public String getName()
    {
        return this.name;
    }
    public Drawable getImage()
    {
        return this.image;
    }
}
