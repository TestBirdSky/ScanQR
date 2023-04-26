package com.qrtb.qrred.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * Date：2023/4/24
 * Describe:
 */
abstract class BaseActivity<B : ViewDataBinding>(val layoutId: Int) : AppCompatActivity() {
    protected lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDensity()
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
        onInitView()
    }

    abstract fun onInitView()


    private fun setDensity() {
        resources.displayMetrics.run {
            val td = heightPixels / 760f
            val dpi = (160 * td).toInt()
            density = td
            scaledDensity = td
            densityDpi = dpi
        }
    }
}