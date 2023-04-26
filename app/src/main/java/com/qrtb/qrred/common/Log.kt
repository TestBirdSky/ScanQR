package com.qrtb.qrred.common

import android.util.Log

/**
 * Date：2023/4/25
 * Describe:
 */

fun String.log(tag: String = "QR_LOG") {
    Log.i(tag, this)
}

fun String.loge(tag: String = "QR_LOG") {
    Log.e(tag, this)
}