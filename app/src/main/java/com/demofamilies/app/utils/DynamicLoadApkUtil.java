package com.demofamilies.app.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.demofamilies.app.R;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * author: xujiaji
 * created on: 2018/5/16 15:28
 * description: 动态加载Apk工具类
 */
public class DynamicLoadApkUtil
{
    public static class PluginBean
    {
        public final String label;
        public final String pakName;
        public final int versionCode;
        public final String versionName;
        public final Drawable icon;

        public PluginBean(String label, String pakName)
        {
            this.label = label;
            this.pakName = pakName;
            versionCode = -1;
            versionName = null;
            icon = null;
        }

        public PluginBean(String label, String pakName, int versionCode, String versionName, Drawable icon)
        {
            this.label = label;
            this.pakName = pakName;
            this.versionCode = versionCode;
            this.versionName = versionName;
            this.icon = icon;
        }
    }

    /**
     * 查找手机内所有的插件
     *
     * @return 返回一个插件List
     */
    public static List<PluginBean> findAllInstalledPlugin(Context context, String myShareUserId)
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
    public static int getInstalledPluginDrawableResourceId(Context pluginContext, String packageName, String resName) throws Exception
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
     *
     * @param pluginPkgName 插件App包名
     */
    public static Context createInstalledPluginContext(Context context, String pluginPkgName) throws PackageManager.NameNotFoundException
    {
        return context.createPackageContext(pluginPkgName,
                Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
    }


    /**
     * 获取未安装apk的信息
     *
     * @param apkFilePath apk文件的路径
     */
    private PluginBean getUninstallApkInfo(Context context, String apkFilePath)
    {

        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
        if (pkgInfo != null)
        {
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
            int versionCode = pkgInfo.versionCode;
            String versionName = pkgInfo.versionName;//版本号
            Drawable icon = pm.getApplicationIcon(appInfo);//图标
            String appName = pm.getApplicationLabel(appInfo).toString();//app名称
            String pkgName = appInfo.packageName;//包名
            return new PluginBean(appName, pkgName, versionCode, versionName, icon);
        }
        return null;
    }


    /**
     * @param apkFilePath apk文件的路径
     * @return 得到对应插件的Resource对象
     */
    public static Resources getUninstallPluginResources(Context context, String apkFilePath)
    {
        try
        {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, apkFilePath);
            Resources superRes = context.getResources();
            return new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建dex类加载器（未安装）
     *
     * @param apkFilePath apk路径
     */
    public static DexClassLoader createDexClassLoader(Context context, String apkFilePath)
    {
        File optimizedDirectoryFile = context.getDir("dex", Context.MODE_PRIVATE);//在应用安装目录下创建一个名为app_dex文件夹目录,如果已经存在则不创建
        //参数：1、包含dex的apk文件或jar文件的路径，2、apk、jar解压缩生成dex存储的目录，3、本地library库目录，一般为null，4、父ClassLoader
        return new DexClassLoader(apkFilePath, optimizedDirectoryFile.getPath(), null, ClassLoader.getSystemClassLoader());
    }

    /**
     * 加载apk获得内部资源
     * @param clazz 资源类class
     * @param resources 资源对象
     * @param resName 资源名字
     */
    public static Drawable getUninstallPluginDrawable(
            Class<?> clazz,
            Resources resources,
            String resName) throws Exception
    {
        //通过使用apk自己的类加载器，反射出R类中相应的内部类进而获取我们需要的资源id
        Field field = clazz.getDeclaredField(resName);//得到名为one的这张图片字段
        int resId = field.getInt(R.id.class);//得到图片id
        return resources.getDrawable(resId);
    }

    /**
     * 获取R$drawable的class实例
     */
    public static Class<?> getUninstallPluginDrawableClass(DexClassLoader classLoader, String apkPackageName) throws Exception
    {
        return classLoader.loadClass(apkPackageName + ".R$drawable");
    }
}
