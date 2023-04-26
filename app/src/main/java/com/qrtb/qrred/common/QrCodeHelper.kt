package com.qrtb.qrred.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.qrtb.qrred.AppQ
import com.qrtb.qrred.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Date：2023/4/25
 * Describe:
 */
object QrCodeHelper {

    suspend fun generateQRCode(data: String, width: Int, height: Int): Bitmap {
        return withContext(Dispatchers.IO) {
            val writer = QRCodeWriter()
            val hints = Hashtable<EncodeHintType, String>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height,hints)
            var bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val grey=AppQ.mApp.resources.getColor(R.color.grey)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else grey)
                }
            }
//            addLogo(bmp)?.let {
//                bmp = it
//            }
            bmp
        }
    }

    private fun addLogo(qrBitmap: Bitmap): Bitmap? {
        val logoBitmap =
            BitmapFactory.decodeResource(AppQ.mApp.resources, R.drawable.app_icon_qr_code)
                ?: return null

        //获取图片的宽高
        val logoWidth: Int = logoBitmap.width
        val logoHeight: Int = logoBitmap.height
        //logo大小为二维码整体大小的1/5
        val scaleFactor = qrBitmap.width * 1.0f / 5 / logoWidth
        var bitmap = Bitmap.createBitmap(qrBitmap.width, qrBitmap.height, Bitmap.Config.ARGB_8888)
        try {
            val canvas = Canvas(bitmap!!)
            canvas.drawBitmap(qrBitmap, 0f, 0f, null)
            canvas.scale(
                scaleFactor,
                scaleFactor,
                (qrBitmap.width / 2).toFloat(),
                (qrBitmap.height / 2).toFloat()
            )
            canvas.drawBitmap(
                logoBitmap,
                ((qrBitmap.width - logoWidth) / 2).toFloat(),
                ((qrBitmap.width - logoHeight) / 2).toFloat(),
                null
            )
            canvas.save()
            canvas.restore()
        } catch (e: Exception) {
            bitmap = null
            e.stackTrace
        }
        return bitmap
    }
}