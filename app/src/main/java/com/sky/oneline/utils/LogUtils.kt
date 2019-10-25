package com.sky.oneline.utils

import android.util.Log
import com.sky.oneline.BuildConfig

class LogUtils {
    companion object {
        fun d(tag: String, content: String) {
            if (BuildConfig.DEBUG)
                Log.d(tag, content)
        }

        fun e(tag: String, content: String) {
            if (BuildConfig.DEBUG)
                Log.d(tag, content)
        }
    }
}