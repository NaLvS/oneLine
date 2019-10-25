package com.sky.oneline.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.sky.oneline.ui.activity.base.BaseActivity
import com.sky.oneline.utils.Checkpoint
import com.sky.oneline.utils.LogUtils
import com.sky.oneline.utils.UiUtils

/**
 * 一笔画的主体View
 */
class OneLineView : LinearLayout, View.OnTouchListener {

    private val tag: String = "OneLineView"

    private var lines = 6//行数
    private var columns = 6//列数
    private var arr = Array(lines) { IntArray(columns) }//用户设置控制点的数组
    private var calculateArray = Array(lines) { IntArray(columns) }//用于计算的数组
    private val paintSelect = Paint()//控制点的Paint
    private val paintStart = Paint()//起点的Paint
    private val paintText = Paint()//文字的Paint
    private val paintArrow = Paint()//箭头的Paint
    private val rectInner: RectF = RectF()//定义一个矩形区域代表一个格子
    private var cellWidth: Float = UiUtils.dip2px(50f).toFloat()//每一个格子的宽度
    private var state: Int = 0//0初始化完成 1可画 2 可选择起始点 3 计算中
    private var lastTouchline = -1//上次手指所在行数
    private var lastTouchColumn = -1//上次手指所在列数
    private var startLine = -1//起点所在的行数
    private var startColumn = -1//起点所在的列数

    constructor(context: Context) : super(context)

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes)

    constructor(context: Context, attributes: AttributeSet, defStyleAttr: Int) : super(context, attributes, defStyleAttr)

    init {
        paintSelect.color = Color.parseColor("#D1D1D1")
        paintSelect.style = Paint.Style.FILL

        paintText.color = Color.parseColor("#333333")
        paintText.style = Paint.Style.FILL
        paintText.textSize = UiUtils.sp2px(20.0f).toFloat()

        paintStart.color = Color.parseColor("#660000FF")
        paintStart.style = Paint.Style.FILL

        paintArrow.color = Color.parseColor("#999999")
        paintArrow.style = Paint.Style.STROKE

        paintArrow.strokeWidth = UiUtils.dip2px(5f).toFloat()
        setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        LogUtils.d(tag, "onTouch...x==${event?.x},y==${event?.y},eventType==${event?.action}")
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                LogUtils.d(tag, "onTouch...ACTION_DOWN")
                dealTouch(event)
            }
            MotionEvent.ACTION_MOVE -> {
                LogUtils.d(tag, "onTouch...ACTION_MOVE")
                dealTouch(event)
            }
            MotionEvent.ACTION_UP -> {
                LogUtils.d(tag, "onTouch...ACTION_UP")
                dealTouch(event)
                lastTouchline = -1
                lastTouchColumn = -1
            }
        }
        return true
    }

    /**
     * 处理触摸事件的方法
     *
     * @param event 触摸事件
     */
    private fun dealTouch(event: MotionEvent?) {
        val x = event!!.x
        val y = event.y
        val cellsX: Int = (x / cellWidth).toInt()
        val cellsY: Int = (y / cellWidth).toInt()
        LogUtils.d(tag, "cellsX==$cellsX,cellsY==$cellsY")
        if (cellsX >= columns || cellsY >= lines) {
            LogUtils.e(tag, "out of the chart!")
            return
        }
        when (state) {
            1 -> {
                if (lastTouchColumn == cellsX && lastTouchline == cellsY) {
                    return
                }
                lastTouchColumn = cellsX
                lastTouchline = cellsY
                selectChart()
            }
            2 -> {
                if (startColumn == cellsX && startLine == cellsY) {
                    return
                }
                startColumn = cellsX
                startLine = cellsY
                invalidate()
            }
            else -> LogUtils.e(tag, "can't touch at this time !")
        }
    }

    /**
     * 获得并选中或反选当前位置对应的数组
     */
    private fun selectChart() {
        if (arr[lastTouchline][lastTouchColumn] == 0) {
            arr[lastTouchline][lastTouchColumn] = 1
        } else {
            arr[lastTouchline][lastTouchColumn] = 0
        }
        invalidate()
    }

    /**
     * 设置行数和列数
     *
     * @param lines 要设置的行数
     * @param columns 要设置的列数
     */
    fun setParams(lines: Int, columns: Int) {
        LogUtils.d(tag, "setParams...lines==$lines,columns==$columns")
        this.columns = columns
        this.lines = lines
        arr = Array(lines) { IntArray(columns) }
        calculateArray = Array(lines) { IntArray(columns) }
        setArrayTo1()
        state = 0
        lastTouchColumn = -1
        lastTouchline = -1
        startLine = -1
        startColumn = -1
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        LogUtils.d(tag, "onMeasure...")
        val availableWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        LogUtils.d(tag, "availableWidth...$availableWidth")
        LogUtils.d(tag, "availableHeight...$availableHeight")
        val cWidth: Float = availableWidth * 1.0f / columns
        val cHeight: Float = availableHeight * 1.0f / lines
        LogUtils.d(tag, "cWidth...$cWidth")
        LogUtils.d(tag, "cHeight...$cHeight")
        cellWidth = if (cWidth < cHeight) cWidth else cHeight
        LogUtils.d(tag, "cellWidth...$cellWidth")
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
    }

    /**
     * 计算这个View的宽度
     *
     * @param measureSpec 计算的参数
     * @return 计算后的宽度
     */
    private fun measureWidth(measureSpec: Int): Int {
        var result: Int
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = (cellWidth * columns).toInt()
            if (specMode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
        }
        return result
    }

    /**
     * 计算这个View的高度
     *
     * @param measureSpec 计算的参数
     * @return 计算后的高度
     */
    private fun measureHeight(measureSpec: Int): Int {
        var result: Int
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = (cellWidth * lines).toInt()
            if (specMode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
        }
        return result
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        LogUtils.d(tag, "onDraw...")
        drawChart(canvas)
        drawRoad(canvas)
    }

    /**
     * 绘制格子
     *
     * @param canvas 画布
     */
    private fun drawChart(canvas: Canvas?) {
        for (line in 0 until lines) {
            for (column in 0 until columns) {
                val left = column * cellWidth
                val top = line * cellWidth
                rectInner.left = left + UiUtils.dip2px(5f)
                rectInner.top = top + UiUtils.dip2px(5f)
                rectInner.bottom = top + cellWidth - UiUtils.dip2px(5f)
                rectInner.right = left + cellWidth - UiUtils.dip2px(5f)
                if (arr[line][column] == 1) {
                    canvas?.drawRoundRect(rectInner, UiUtils.dip2px(5f).toFloat(), UiUtils.dip2px(5f).toFloat(), paintSelect)
                }
                if (line == startLine && column == startColumn) {
                    canvas?.drawRoundRect(rectInner, UiUtils.dip2px(5f).toFloat(), UiUtils.dip2px(5f).toFloat(), paintStart)
                }
                val text = calculateArray[line][column].toString()
                val textWidth = paintText.measureText(text)
                canvas?.drawText(text, left + (cellWidth - textWidth) / 2, top + (cellWidth + UiUtils.px2sp(paintText.textSize)) / 2, paintText)
            }
        }
    }

    /**
     * 在计算完成之后绘制路径
     *
     * @param canvas 画布
     */
    private fun drawRoad(canvas: Canvas?) {
        for (line in 0 until calculateArray.size) {
            for (column in 0 until calculateArray[line].size) {
                val nextLine = line + 1
                val nextColumn = column + 1
                val current = calculateArray[line][column]
                if (current > 0) {
                    if (nextLine < lines) {
                        val nextLineNum = calculateArray[nextLine][column]
                        if (nextLineNum > 0) {
                            val currentCenterX = cellWidth * column + cellWidth / 2
                            val currentCenterY = cellWidth * line + cellWidth / 2
                            if (current == nextLineNum - 1) {
                                drawArrow(canvas, currentCenterX, currentCenterY, currentCenterX, currentCenterY + cellWidth, paintArrow)
                            } else if (current == nextLineNum + 1) {
                                drawArrow(canvas, currentCenterX, currentCenterY + cellWidth, currentCenterX, currentCenterY, paintArrow)
                            }
                        }
                    }
                    if (nextColumn < columns) {
                        val nextColumnNum = calculateArray[line][nextColumn]
                        if (nextColumnNum > 0) {
                            val currentCenterX = cellWidth * column + cellWidth / 2
                            val currentCenterY = cellWidth * line + cellWidth / 2
                            if (current == nextColumnNum - 1) {
                                drawArrow(canvas, currentCenterX, currentCenterY, currentCenterX + cellWidth, currentCenterY, paintArrow)
                            } else if (current == nextColumnNum + 1) {
                                drawArrow(canvas, currentCenterX + cellWidth, currentCenterY, currentCenterX, currentCenterY, paintArrow)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 画箭头
     *
     * @param sx 开始位置的X坐标
     * @param sy 开始位置的Y坐标
     * @param ex 途径点的X坐标
     * @param ey 途径点的Y坐标
     * @param paint 画笔
     */
    private fun drawArrow(canvas: Canvas?, sx: Float, sy: Float, ex: Float, ey: Float, paint: Paint) {
        val size = 11
        val count = 40
        val x = ex - sx
        val y = ey - sy
        val d = (x * x + y * y).toDouble()
        val r = Math.sqrt(d)
        val zx = (ex - count * x / r).toFloat()
        val zy = (ey - count * y / r).toFloat()
        val xz = zx - sx
        val yz = zy - sy
        val zd = (xz * xz + yz * yz).toDouble()
        val zr = Math.sqrt(zd)
        val triangle = Path()
        triangle.moveTo(sx, sy)
        triangle.lineTo((zx + size * yz / zr).toFloat(), (zy - size * xz / zr).toFloat())
        triangle.lineTo((zx + size.toFloat() * 2f * yz / zr).toFloat(), (zy - size.toFloat() * 2f * xz / zr).toFloat())
        triangle.lineTo(ex, ey)
        triangle.lineTo((zx - size.toFloat() * 2f * yz / zr).toFloat(), (zy + size.toFloat() * 2f * xz / zr).toFloat())
        triangle.lineTo((zx - size * yz / zr).toFloat(), (zy + size * xz / zr).toFloat())
        triangle.close()
        canvas?.drawPath(triangle, paint)
    }

    /**
     * 开始选择控制点
     */
    fun startSet() {
        arr = Array(lines) { IntArray(columns) }
        setArrayTo1()
        calculateArray = Array(lines) { IntArray(columns) }
        state = 1
        lastTouchColumn = -1
        lastTouchline = -1
        startLine = -1
        startColumn = -1
        LogUtils.d(tag, "startSet ...array is ${printArray()}")
        invalidate()
    }

    /**
     * 选择开始点坐标
     */
    fun setStartPoint() {
        when (state) {
            1 -> state = 2
            4 -> {
                startColumn = -1
                startLine = -1
                state = 2
                invalidate()
            }
            else -> {
                showToast("Set control points first!")
                LogUtils.e(tag, "start set start point but state is $state")
            }
        }
        LogUtils.d(tag, "setStartPoint ...array is ${printArray()}")
    }

    /**
     * 显示Toast
     *
     * @param text 要显示的文字
     */
    private fun showToast(text: String) {
        (context as BaseActivity).showToast(text)
    }

    /**
     * 开始计算
     */
    fun start() {
        if (startLine < 0 || startColumn < 0 || startLine >= lines || startColumn >= columns) {
            LogUtils.e(tag, "invalid start point")
            return
        }
        LogUtils.d(tag, "start ...array is ${printArray()}")
        if (state == 2) {
            state = 3
            for (line in 0 until lines) {
                for (column in 0 until columns) {
                    if (arr[line][column] == 1) {
                        calculateArray[line][column] = 0
                    } else if (arr[line][column] == 0) {
                        calculateArray[line][column] = -1
                    }
                }
            }
            val checkPoint = Checkpoint(calculateArray, startLine, startColumn)
            val timerDialog = TimerDialog(context)
            checkPoint.calculate(object : Checkpoint.Calculate {
                override fun onCalculateStart() {
                    timerDialog.show()
                }

                override fun onCalculateEnd() {
                    timerDialog.disMiss()
                    if (checkPoint.hasWin()) {
                        invalidate()
                    } else {
                        showToast("Can't find a road!")
                    }
                    state = 4
                }
            })
        } else {
            showToast("Set start point first!")
            LogUtils.e(tag, "start calculate but state is $state")
        }
    }

    /**
     * 将Array中的所有值都置为1
     */
    private fun setArrayTo1() {
        for (line in 0 until arr.size) {
            for (col in 0 until arr[0].size) {
                arr[line][col] = 1
            }
        }
    }

    /**
     * 打印数组
     *
     * @return 数组转化成的字符串
     */
    private fun printArray(): String {
        val builder = StringBuilder()
        builder.append("{")
        for (line in 0 until arr.size) {
            builder.append("{")
            for (col in 0 until arr[0].size) {
                builder.append("${arr[line][col]}").append(",")
            }
            builder.append("}")
        }
        builder.append("}")
        return builder.toString()
    }
}