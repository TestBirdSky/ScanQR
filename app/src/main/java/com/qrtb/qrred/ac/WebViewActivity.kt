package com.qrtb.qrred.ac

import com.qrtb.qrred.R
import com.qrtb.qrred.base.BaseActivity
import com.qrtb.qrred.databinding.ActivityWebViewBinding

/**
 * Date：2023/4/25
 * Describe:
 */
class WebViewActivity:BaseActivity<ActivityWebViewBinding>(R.layout.activity_web_view) {
    override fun onInitView() {
        binding.run {
            ivBack.setOnClickListener {
                onBackPressed()
            }
            webView.loadUrl("")
        }
    }
}