package com.qrtb.qrred.common

import android.app.Activity
import android.app.AlertDialog
import android.os.Build
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

/**
 * Date：2023/4/24
 * Describe:
 */

fun Activity.checkReadMediaPermission(checkFinish: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        checkFinish.invoke()
    } else {
        XXPermissions.with(this).permission(Permission.READ_MEDIA_IMAGES)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    if (allGranted) {
                        checkFinish.invoke()
                    }
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    super.onDenied(permissions, doNotAskAgain)
                    if (doNotAskAgain) {
                        startPermissionTips( permissions)
                    }
                }
            })
    }
}

fun Activity.checkCameraPermission(checkFinish: () -> Unit) {
    XXPermissions.with(this).permission(Permission.CAMERA).request(object : OnPermissionCallback {
        override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
            if (allGranted) {
                checkFinish.invoke()
            }
        }

        override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
            super.onDenied(permissions, doNotAskAgain)
            if (doNotAskAgain) {
                startPermissionTips( permissions)
            }
        }
    })
}

fun Activity.startPermissionTips(permissions: MutableList<String>) {
    AlertDialog.Builder(this@startPermissionTips)
        .setMessage("Permission acquisition failed. The function cannot be used properly. Do you want to obtain it again?")
        .setPositiveButton("Go") { dialog, _ ->
            dialog.dismiss()
            XXPermissions.startPermissionActivity(
                this@startPermissionTips, permissions
            )
        }.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }.create().show()
}