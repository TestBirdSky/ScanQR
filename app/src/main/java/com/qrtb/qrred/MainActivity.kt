package com.qrtb.qrred

import com.qrtb.qrred.ac.CreateActivity
import com.qrtb.qrred.ac.ScanningActivity
import com.qrtb.qrred.ac.SettingActivity
import com.qrtb.qrred.base.BaseActivity
import com.qrtb.qrred.common.checkCameraPermission
import com.qrtb.qrred.common.toActivity
import com.qrtb.qrred.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    override fun onInitView() {
        binding.run {
            ivSetting.setOnClickListener {
                toActivity<SettingActivity>()
            }
            ivScanner.setOnClickListener {
                checkCameraPermission{
                    toActivity<ScanningActivity>()
                }
            }
            ivCreate.setOnClickListener {
                toActivity<CreateActivity>()
            }
        }
    }
}