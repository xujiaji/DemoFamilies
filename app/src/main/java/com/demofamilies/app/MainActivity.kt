package com.demofamilies.app

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.demofamilies.app.natives.NativeUtil
import com.morgoo.droidplugin.pm.PluginManager
import com.morgoo.helper.compat.PackageManagerCompat.INSTALL_FAILED_NOT_SUPPORT_ABI
import com.morgoo.helper.compat.PackageManagerCompat.INSTALL_SUCCEEDED
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//         Example of a call to a native method
        sample_text.text = NativeUtil.stringFromJNI()



        val apkPath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "plugin.apk"
        val re = PluginManager.getInstance().installPackage(apkPath, 0)
        val info = packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES)
        Log.e("MainActivity", "apkPath = $apkPath")
        Log.e("MainActivity", "info = $info")
        when (re) {
            PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION -> Toast.makeText(this, "安装失败，文件请求的权限太多", Toast.LENGTH_SHORT).show()
            INSTALL_FAILED_NOT_SUPPORT_ABI -> Toast.makeText(this, "宿主不支持插件的abi环境，可能宿主运行时为64位，但插件只支持32位", Toast.LENGTH_SHORT).show()
            INSTALL_SUCCEEDED -> {
                Toast.makeText(this, "安装完成", Toast.LENGTH_SHORT).show()
                val pm = this.packageManager
                val intent = pm.getLaunchIntentForPackage(info.packageName)
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    Log.i("DroidPlugin", "start " + info.packageName + "@" + intent)
                    startActivity(intent)
                } else {
                    Log.e("DroidPlugin", "pm " + pm.toString() + " no find intent " + info.packageName)
                }
            }

        }

        img.setOnClickListener {
            //            RePlugin.install(apkPath)
            val intent = packageManager.getLaunchIntentForPackage(info.packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                Log.i("DroidPlugin", "start " + info.packageName + "@" + intent)
                startActivity(intent)
            }
        }
        img2.setOnClickListener {
//            RePlugin.startActivity(MainActivity@this,
//                    RePlugin.createIntent(
//                            "solid.ren.themeskinning",
//                            "solid.ren.themeskinning.activity.MainActivity"))
        }
    }


}
