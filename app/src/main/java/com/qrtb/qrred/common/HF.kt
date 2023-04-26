package com.qrtb.qrred.common

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Process
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.WorkerThread
import com.qrtb.qrred.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Date：2023/4/24
 * Describe:
 */

inline fun <reified A : Activity> Activity.toActivity(
    bundle: Bundle? = null, requestCode: Int? = null
) {
    val intent = Intent(this, A::class.java)
    bundle?.let { intent.putExtras(it) }
    requestCode?.let {
        startActivityForResult(intent, requestCode)
    } ?: run {
        startActivity(intent)
    }
}

fun Context.shareTextToOtherApp(msg: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(
        Intent.EXTRA_TEXT, msg
    )
    startActivity(Intent.createChooser(intent, "share"))
}

fun Context.jumpEmailApp(ownerEmail: String) {
    val s = "mailto:${ownerEmail}"
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse(s)
    if (isIntentAvailable(Intent.ACTION_SENDTO)) {
        startActivity(intent)
    } else {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(null, ownerEmail)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(
            this, "Copy contact us email successful", Toast.LENGTH_SHORT
        ).show()
    }
}

fun Context.isMainProgress(): Boolean {
    val manager = getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
    for (processInfo in manager.runningAppProcesses) {
        if (processInfo.pid == Process.myPid()) {
            return processInfo.processName == packageName
        }
    }
    return false
}

fun Context.isIntentAvailable(action: String): Boolean {
    return Intent(action).resolveActivity(packageManager) != null
}


fun Context.getPackInfo(): PackageInfo {
    val pm = packageManager
    return pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
}

fun Context.shareAppDownPath() {
    shareTextToOtherApp("https://play.google.com/store/apps/details?id=${getPackInfo().packageName}")
}

fun Context.jumpGooglePlay() {
    val packName = getPackInfo().packageName
    Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(
            "https://play.google.com/store/apps/details?id=$packName"
        )
        startActivity(this)
    }
}

fun Context.dpToPx(dp: Int): Int = (resources.displayMetrics.density * dp).toInt()
fun Context.dpToPx2(dp: Int): Float = (resources.displayMetrics.density * dp)


@WorkerThread
fun Context.saveBitmapToMedia( bitmap: Bitmap, saveSuccess: () -> Unit = {}) {
    val filename = "qr-image${System.currentTimeMillis()}.jpg"
    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val file = File(path, filename)
    runCatching {
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
        MediaScannerConnection.scanFile(
            applicationContext, arrayOf(file.absolutePath), null
        ) { path, uri ->
            // 扫描完成后的操作
            saveSuccess.invoke()
        }
    }.onFailure {
        saveBitmapToMedia2( bitmap, saveSuccess)
    }
}

private fun Context.saveBitmapToMedia2(bitmap: Bitmap, saveSuccess: () -> Unit = {}) {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path: String = MediaStore.Images.Media.insertImage(
        applicationContext.contentResolver,
        bitmap,
        resources.getString(R.string.app_name),
        "qr code image"
    )
    MediaScannerConnection.scanFile(
        applicationContext, arrayOf(path), null
    ) { _, uri ->
        // 扫描完成后的操作
        saveSuccess.invoke()
    }
}

fun Context.shareBitmapToOtherApp(bitmap: Bitmap) {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path: String = MediaStore.Images.Media.insertImage(
        applicationContext.contentResolver,
        bitmap,
        resources.getString(R.string.app_name),
        "qr code Picture"
    )
    "shareBitmapToOtherApp -->saveBitmapToMedia -->$path".log()
    val uri = Uri.parse(path)
    Intent().apply {
        type = "image/*"
        action = Intent.ACTION_SEND
        addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(
            Intent.createChooser(
                this, resources.getString(R.string.app_name)
            )
        )
    }
}
