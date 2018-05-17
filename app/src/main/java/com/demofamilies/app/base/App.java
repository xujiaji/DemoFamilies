package com.demofamilies.app.base;

import android.app.Application;
import android.content.Context;

import com.morgoo.droidplugin.PluginHelper;

/**
 * author: xujiaji
 * created on: 2018/5/17 14:29
 * description:
 */
public class App extends Application
{
    private static Application mInstance;

    @Override
    public void onCreate()
    {
        super.onCreate();
        PluginHelper.getInstance().applicationOnCreate(getBaseContext());
        mInstance = this;
    }

    @Override
    protected void attachBaseContext(Context base)
    {
        PluginHelper.getInstance().applicationAttachBaseContext(base);
        super.attachBaseContext(base);
    }

    public static Context getInstance()
    {
        return mInstance;
    }
}
