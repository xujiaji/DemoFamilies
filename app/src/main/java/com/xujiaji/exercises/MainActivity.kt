package com.xujiaji.exercises

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.xujiaji.exercises.natives.NativeUtil
import com.xujiaji.exercises.utils.DynamicLoadApkUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//         Example of a call to a native method
        sample_text.text = NativeUtil.stringFromJNI()

        val datas = ArrayList<HashMap<String, String>>()
        val plugins = DynamicLoadApkUtil.findAllPlugin(this, "com.xujiaji.app")
        if (plugins != null && !plugins.isEmpty())
        {
            for (plugin in plugins)
            {
                val map = HashMap<String, String>()
                map["label"] = plugin.label
                map["pkg"] = plugin.pakName
                datas.add(map)
            }
            sample_text.text = datas.toString()
            showImg(datas[0]["pkg"]!!)
        } else
        {
            Toast.makeText(this, "没有找到插件，请先下载！", Toast.LENGTH_LONG).show()
        }

    }

    private fun showImg(pluginPkgName: String)
    {
        val pluginContext = DynamicLoadApkUtil.createPluginContext(this, pluginPkgName)
        val resId = DynamicLoadApkUtil.getDrawableResourceId(pluginContext, pluginPkgName, "ic_beach")
        img.setImageDrawable(pluginContext.resources.getDrawable(resId))
    }
}
