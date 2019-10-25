package com.sky.oneline.ui.activity.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import butterknife.ButterKnife
import com.sky.oneline.utils.LogUtils

abstract class BaseActivity : AppCompatActivity() {

    abstract val TAG: String

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        beforeSetContent()
        setContentView(getLayout())
        ButterKnife.bind(this)
        initView(savedInstanceState)
    }

    final override fun onDestroy() {
        beforeDestroy()
        super.onDestroy()
    }

    /**
     * 获取布局文件的Id
     *
     * @return 布局文件的layout Id
     */
    abstract fun getLayout(): Int

    /**
     * 进行初始化工作
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 在Activity的onDestroy()调用之前调用，回收资源
     */
    open fun beforeDestroy() {}

    /**
     * 在Activity的setContentView被调用之前调用，处理一些特殊的操作
     */
    open fun beforeSetContent() {}

    private var toast: Toast? = null

    /**
     * 显示Toast
     *
     * @param text 要显示的文字
     */
    fun showToast(text: String) {
        LogUtils.d(TAG, "show Toast text =$text")
        if (toast == null) {
            toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        }
        toast!!.setText(text)
        toast!!.show()
    }
}