package com.xujiaji.test_android_plugin;

import android.content.Context;
import android.widget.Toast;

/**
 * author: xujiaji
 * created on: 2018/5/16 14:23
 * description:
 */
public class ShowToastImpl implements IShowToast
{
    @Override
    public int showToast(Context context)
    {
        Toast.makeText(context, "我来自另一个dex文件", Toast.LENGTH_SHORT).show();
        return 100;
    }
}
