package com.sky.oneline.utils

import android.os.Handler

class Checkpoint
/**
 * array 0代表起点，1-n代表行走顺序，-1代表不可达
 *
 * @param array array
 */
(val array: Array<IntArray>, var startX: Int, var startY: Int) {

    private val row: Int = array.size
    private val column: Int = array[0].size
    private val handler: Handler = Handler(Handler.Callback {
        when (it.what) {
            0 -> {
                val obj: Calculate = it.obj as Calculate
                obj.onCalculateStart()
            }
            1 -> {
                val obj: Calculate = it.obj as Calculate
                obj.onCalculateEnd()
            }
        }
        return@Callback true
    })

    private var step = 1
    private var hasWin = false

    init {
        this.array[startX][startY] = 1
    }

    fun hasWin(): Boolean {
        for (i in 0 until row) {
            for (j in 0 until column) {
                if (array[i][j] == 0) {
                    return false
                }
            }
        }
        hasWin = true
        return true
    }

    private fun solve(nowX: Int, nowY: Int) {
        if (hasWin())
            return
        if (!hasWin && nowY > 0 && array[nowX][nowY - 1] == 0) {
            array[nowX][nowY - 1] = ++step
            solve(nowX, nowY - 1)
        }//left
        if (!hasWin && nowY < column - 1 && array[nowX][nowY + 1] == 0) {
            array[nowX][nowY + 1] = ++step
            solve(nowX, nowY + 1)
        }//right
        if (!hasWin && nowX > 0 && array[nowX - 1][nowY] == 0) {
            array[nowX - 1][nowY] = ++step
            solve(nowX - 1, nowY)
        }//up
        if (!hasWin && nowX < row - 1 && array[nowX + 1][nowY] == 0) {
            array[nowX + 1][nowY] = ++step
            solve(nowX + 1, nowY)
        }//down
        if (!hasWin && array[nowX][nowY] != 1) {
            array[nowX][nowY] = 0
            step--
        }
    }

    fun calculate(listener: Calculate) {
        Thread {
            val messageStart = handler.obtainMessage()
            messageStart.what = 0
            messageStart.obj = listener
            handler.sendMessage(messageStart)

            solve(startX, startY)

            val messageEnd = handler.obtainMessage()
            messageEnd.what = 1
            messageEnd.obj = listener
            handler.sendMessage(messageEnd)
        }.start()
    }

    interface Calculate {
        fun onCalculateStart()
        fun onCalculateEnd()
    }
}
