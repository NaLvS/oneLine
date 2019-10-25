package com.sky.oneline.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import com.sky.oneline.App


class UiUtils {
    companion object {
        fun getScreenWidth(): Int {
            val wm = getAppContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            wm.defaultDisplay.getMetrics(dm)
            return dm.widthPixels
        }

        fun getScreenHeight(): Int {
            val wm = getAppContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            wm.defaultDisplay.getMetrics(dm)
            return dm.heightPixels
        }

        private fun getAppContext(): Context {
            return App.getAppContext()
        }

        /**
         * 根据手机分辨率从DP转成PX
         * @param dpValue
         * @return
         */
        fun dip2px(dpValue: Float): Int {
            val scale = getAppContext().resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }

        /**
         * 将sp值转换为px值，保证文字大小不变
         * @param spValue
         * @return
         */
        fun sp2px(spValue: Float): Int {
            val fontScale = getAppContext().resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }

        /**
         * 根据手机的分辨率PX(像素)转成DP
         * @param pxValue
         * @return
         */
        fun px2dip(pxValue: Float): Int {
            val scale = getAppContext().resources.displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }

        /**
         * 将px值转换为sp值，保证文字大小不变
         * @param pxValue
         * @return
         */
        fun px2sp(pxValue: Float): Int {
            val fontScale = getAppContext().resources.displayMetrics.scaledDensity
            return (pxValue / fontScale + 0.5f).toInt()
        }
    }
}