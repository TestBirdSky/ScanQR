package com.qrtb.qrred

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.qrtb.qrred.ac.LauncherActivity
import com.qrtb.qrred.common.log
import com.qrtb.qrred.common.toActivity

/**
 * Date：2023/4/25
 * Describe:
 */
var isAppResume = false
var isHotReboot = true

class AppActivityLifecycle : Application.ActivityLifecycleCallbacks {
    private var num = 0
    private var onBackgroundTime = -1L
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        "onActivityStarted->$activity".log()
        if (isHotReboot && onBackgroundTime != -1L && System.currentTimeMillis() - onBackgroundTime > 3000) {
            activity.toActivity<LauncherActivity>()
            if (activity is LauncherActivity) {
                activity.finish()
            }
        }
        onBackgroundTime = -1L
        isAppResume = true
        num++
    }

    override fun onActivityResumed(activity: Activity) {
        "onActivityResumed->$activity".log()
    }

    override fun onActivityPaused(activity: Activity) {
        "onActivityPaused->$activity".log()
    }

    override fun onActivityStopped(activity: Activity) {
        "onActivityStopped->$activity".log()
        num--
        if (num <= 0) {
            isAppResume = false
            if (isHotReboot)
                onBackgroundTime = System.currentTimeMillis()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        "onActivityDestroyed->$activity".log()
    }
}