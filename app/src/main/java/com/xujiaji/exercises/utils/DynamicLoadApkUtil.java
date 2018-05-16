package com.xujiaji.exercises.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.xujiaji.exercises.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.PathClassLoader;

import static android.content.Context.CONTEXT_IGNORE_SECURITY;
import static android.content.Context.CONTEXT_INCLUDE_CODE;

/**
 * author: xujiaji
 * created on: 2018/5/16 15:28
 * description: 系统工具类
 */
public class DynamicLoadApkUtil
{
    public static class PluginBean
    {
        public String label;
        public String pakName;

        public PluginBean(String label, String pakName)
        {
            this.label = label;
            this.pakName = pakName;
        }
    }

    /**
     * 查找手机内所有的插件
     *
     * @return 返回一个插件List
     */
    public static List<PluginBean> findAllPlugin(Context context, String myShareUserId)
    {
        List<PluginBean> plugins = new ArrayList<>();
        PackageManager pm = context.getPackageManager();

        //通过包管理器查找所有已安装的apk文件
        List<PackageInfo> packageInfoList = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo info : packageInfoList)
        {
            //得到当前apk的包名
            String pkgName = info.packageName;
            //得到当前apk的sharedUserId
            String shareUserId = info.sharedUserId;
            //判断这个apk是否是我们应用程序的插件
            if (shareUserId != null && shareUserId.equals(myShareUserId) && !pkgName.equals(context.getPackageName()))
            {
                String label = pm.getApplicationLabel(info.applicationInfo).toString();//得到插件apk的名称
                PluginBean bean = new PluginBean(label, pkgName);
                plugins.add(bean);
            }
        }
        return plugins;
    }

    /**
     * 加载已安装的apk
     *
     * @param packageName   应用的包名
     * @param pluginContext 插件app的上下文
     * @return 对应资源的id
     */
    public static int getDrawableResourceId(Context pluginContext, String packageName, String resName) throws Exception
    {
        //第一个参数为包含dex的apk或者jar的路径，第二个参数为父加载器
        PathClassLoader pathClassLoader =
                new PathClassLoader(pluginContext.getPackageResourcePath(), ClassLoader.getSystemClassLoader());
        Class<?> clazz = pathClassLoader.loadClass(packageName + ".R$drawable");
//        //参数：1、类的全名，2、是否初始化类，3、加载时使用的类加载器
//        Class<?> clazz = Class.forName(packageName + ".R$drawable", true, pathClassLoader);
        //使用上述两种方式都可以，这里我们得到R类中的内部类mipmap，通过它得到对应的图片id，进而给我们使用
        Field field = clazz.getDeclaredField(resName);
        return field.getInt(R.mipmap.class);
    }

    /**
     * 创建插件app上下文
     * @param pluginPkgName 插件App包名
     */
    public static Context createPluginContext(Context context, String pluginPkgName) throws PackageManager.NameNotFoundException
    {
        return context.createPackageContext(pluginPkgName,
                Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
    }
}
