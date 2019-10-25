package com.sky.oneline.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Spinner
import com.sky.oneline.R
import com.sky.oneline.ui.activity.base.BaseActivity
import com.sky.oneline.utils.bindView
import com.sky.oneline.widget.SetOneLineView

class OneLineActivity : BaseActivity(), OnItemSelectedListener {
    override val TAG: String
        get() = "OneLineActivity"
    private val spLines: Spinner by bindView(R.id.sp_lines)
    private val spColumns: Spinner by bindView(R.id.sp_cols)
    private val olvCenter: SetOneLineView by bindView(R.id.sol_center)

    private var lines = 0
    private var columns = 0

    override fun getLayout(): Int {
        return R.layout.activity_one_line
    }

    override fun initView(savedInstanceState: Bundle?) {
        spLines.setSelection(5)
        spColumns.setSelection(5)
        spLines.onItemSelectedListener = this
        spColumns.onItemSelectedListener = this
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.sp_lines -> {
                val itemAtPosition: String = parent.getItemAtPosition(position) as String
                lines = itemAtPosition.toInt()
                olvCenter.setParams(lines, columns)
            }
            R.id.sp_cols -> {
                val itemAtPosition: String = parent.getItemAtPosition(position) as String
                columns = itemAtPosition.toInt()
                olvCenter.setParams(lines, columns)
            }
        }
    }
}