package com.qrtb.qrred.common

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.qrtb.qrred.isHotReboot

/**
 * Date：2023/4/25
 * Describe:
 */
fun AppCompatActivity.registerOpenGallery(callBack: (intent: Intent) -> Unit): ActivityResultLauncher<Any?> {
    return registerForActivityResult(object : ActivityResultContract<Any?, Intent?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Intent? {
            return if (resultCode == AppCompatActivity.RESULT_OK) {
                intent
            } else {
                null
            }
        }
    }) {
        isHotReboot = true
        if (it != null) {
            callBack.invoke(it)
        }
    }
}