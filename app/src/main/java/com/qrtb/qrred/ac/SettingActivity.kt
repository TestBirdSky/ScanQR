package com.qrtb.qrred.ac

import com.qrtb.qrred.R
import com.qrtb.qrred.base.BaseActivity
import com.qrtb.qrred.common.jumpEmailApp
import com.qrtb.qrred.common.jumpGooglePlay
import com.qrtb.qrred.common.shareAppDownPath
import com.qrtb.qrred.common.toActivity
import com.qrtb.qrred.databinding.AcSettingBinding

/**
 * Date：2023/4/24
 * Describe:
 */
class SettingActivity : BaseActivity<AcSettingBinding>(R.layout.ac_setting) {
    override fun onInitView() {
        binding.run {
            ivBack.setOnClickListener { onBackPressed() }
            tvContact.setOnClickListener {
                jumpEmailApp("")
            }
            tvPrivacy.setOnClickListener {
                toActivity<WebViewActivity>()
            }
            tvShare.setOnClickListener {
                shareAppDownPath()
            }
            tvUpdate.setOnClickListener {
                jumpGooglePlay()
            }
        }
    }
}