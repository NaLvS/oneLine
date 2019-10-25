package com.sky.oneline.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import butterknife.ButterKnife
import butterknife.OnClick
import com.sky.oneline.R
import com.sky.oneline.utils.bindView

class SetOneLineView : LinearLayout {
    private val olvCenter: OneLineView by bindView(R.id.olv_center)

    constructor(context: Context) : super(context)

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes)

    constructor(context: Context, attributes: AttributeSet, defStyleAttr: Int) : super(context, attributes, defStyleAttr)

    init {
        View.inflate(context, R.layout.item_set_one_line, this)
        ButterKnife.bind(this)
    }

    @OnClick(R.id.btn_set_start, R.id.btn_set_start_point, R.id.btn_start)
    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_set_start -> {
                olvCenter.startSet()
            }
            R.id.btn_set_start_point -> {
                olvCenter.setStartPoint()
            }
            R.id.btn_start -> {
                olvCenter.start()
            }
        }
    }

    /**
     * 设置行列数
     *
     * @param linesInt 行数
     * @param columnsInt 列数
     */
    fun setParams(linesInt: Int, columnsInt: Int) {
        olvCenter.setParams(linesInt, columnsInt)
    }
}