package com.qrtb.qrred

import android.app.Application
import com.tencent.mmkv.MMKV

/**
 * Date：2023/4/24
 * Describe:
 */
class AppQ : Application() {
    companion object {
        lateinit var mApp: AppQ
            private set
    }

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        mApp = this
        registerActivityLifecycleCallbacks(AppActivityLifecycle())
    }
}