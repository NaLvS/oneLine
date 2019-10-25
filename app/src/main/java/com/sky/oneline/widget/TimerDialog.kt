package com.sky.oneline.widget

import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import butterknife.ButterKnife
import butterknife.OnClick
import com.sky.oneline.R
import com.sky.oneline.utils.UiUtils
import java.text.SimpleDateFormat
import java.util.*

class TimerDialog(context: Context) : Handler.Callback {
    private var tvTime: TextView

    private val dialog: Dialog
    private val handler: Handler = Handler(this)

    private var pastTime: Long = 0L
    private var isShowing = false

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_timer, null)
        ButterKnife.bind(view)
        val rootView = view.findViewById<View>(R.id.lLayout_bg)
        tvTime = view.findViewById(R.id.tv_time)
        dialog = Dialog(context, R.style.CustomAlertDialogStyle)
        dialog.setContentView(view)
        dialog.setCancelable(false)
        rootView.layoutParams = FrameLayout.LayoutParams((UiUtils.getScreenWidth() * 0.85).toInt(), LayoutParams.WRAP_CONTENT)
    }

    override fun handleMessage(msg: Message?): Boolean {
        pastTime += 1000
        tvTime.text = getFormattedTime(pastTime)
        if (isShowing) {
            handler.sendEmptyMessageDelayed(0, 1000)
        } else {
            handler.removeMessages(0)
            dialog.dismiss()
        }
        return true
    }

    fun show() {
        isShowing = true
        handler.sendEmptyMessage(0)
        dialog.show()
    }

    private fun getFormattedTime(time: Long): String {
        val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        return formatter.format(time)
    }

    fun disMiss() {
        isShowing = false
        handler.sendEmptyMessage(0)
    }
}
