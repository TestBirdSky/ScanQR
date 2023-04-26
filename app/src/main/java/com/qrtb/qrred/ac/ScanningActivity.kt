package com.qrtb.qrred.ac

import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.qrtb.qrred.R
import com.qrtb.qrred.base.BaseActivity
import com.qrtb.qrred.common.dpToPx2
import com.qrtb.qrred.common.log
import com.qrtb.qrred.common.registerOpenGallery
import com.qrtb.qrred.common.toActivity
import com.qrtb.qrred.databinding.ActivityScanningBinding
import com.qrtb.qrred.isHotReboot

/**
 * Date：2023/4/24
 * Describe:
 */
class ScanningActivity : BaseActivity<ActivityScanningBinding>(R.layout.activity_scanning) {
    private val scanningAnimation by lazy {
        TranslateAnimation(
            0f, 0f, 0f,
            dpToPx2(260)
        ).apply {
            duration = 2000
            repeatCount = -1
        }
    }
    private val openGallery = registerOpenGallery {
        val imageUri = it.data
        // 从 URI 获取所选图像的 Bitmap
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
        analyzerQrCode(bitmap)
    }
    private lateinit var cameraController: LifecycleCameraController
    private lateinit var barcodeScanner: BarcodeScanner
    private var isOpenTorch = false
    override fun onInitView() {
        binding.run {
            ivBack.setOnClickListener {
                onBackPressed()
            }
            ivGallery.setOnClickListener {
                animationCancel()
                isHotReboot = false
                openGallery.launch(null)
            }
            ivLight.setOnClickListener {
                cameraController.enableTorch(!isOpenTorch)
            }
        }
        startCamera()
        cameraController.torchState.observe(this) {
            isOpenTorch = it == 1
            binding.ivLight.setImageResource(
                if (isOpenTorch) R.drawable.ic_switch_light_open
                else R.drawable.ic_light_switch
            )
        }
        animationStart()
    }

    private fun animationStart() {
        binding.ivScanningAnimator.animation?.cancel()
        binding.ivScanningAnimator.visibility = View.VISIBLE
        binding.ivScanningAnimator.animation = scanningAnimation
        binding.ivScanningAnimator.animation.start()
    }

    private fun animationCancel() {
        binding.ivScanningAnimator.animation?.cancel()
        binding.ivScanningAnimator.visibility = View.GONE
    }

    private fun startCamera() {
        cameraController = LifecycleCameraController(baseContext)
        val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            //条形码
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_CODE_39,
            Barcode.FORMAT_CODE_93,
            Barcode.FORMAT_CODABAR,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_AZTEC
        ).build()
        barcodeScanner = BarcodeScanning.getClient(options)
        cameraController.setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(this),
            MlKitAnalyzer(
                listOf(barcodeScanner),
                CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED,
                ContextCompat.getMainExecutor(this)
            ) { result: MlKitAnalyzer.Result? ->
                val barcodeResults = result?.getValue(barcodeScanner)
                if ((barcodeResults == null) || (barcodeResults.size == 0) || (barcodeResults.first() == null)) {
                    return@MlKitAnalyzer
                }
                analyzerBorder(barcodeResults)
            })
        cameraController.bindToLifecycle(this)
        binding.previewView.controller = cameraController
    }

    private fun analyzerBorder(barcodeList: List<Barcode>, isAnalyzerGallery: Boolean = false) {
        val stringBuilder = StringBuilder()
        var type = Barcode.TYPE_TEXT
        for (barcode in barcodeList) {
            when (barcode.valueType) {
                Barcode.TYPE_URL -> {
                    stringBuilder.append(barcode.url?.url)
                    type = Barcode.TYPE_URL
                    break
                }
                else -> {
                    type = barcode.valueType
                    stringBuilder.append(barcode.rawValue.toString())
                }
            }
        }
        if (isAnalyzerGallery && stringBuilder.isEmpty()) {
            Toast.makeText(this, "QR code recognition failed!", Toast.LENGTH_LONG).show()
            animationStart()
        }
//        "analyzerBorder--->$stringBuilder --${barcodeList.size}".log()
        if (stringBuilder.isNotEmpty() && stringBuilder.toString() != "null") {
            jumpToResult(stringBuilder.toString(), type)
        }
    }

    private fun jumpToResult(result: String, type: Int) {
        cameraController.unbind()
        toActivity<ScanResultActivity>(bundle = Bundle().apply {
            putString("result", result)
            putInt("type", type)
        })
        finish()
    }

    private fun analyzerQrCode(bitmap: Bitmap) {
        val scanner = BarcodeScanning.getClient()
        val image: InputImage = InputImage.fromBitmap(bitmap, 0)
        scanner.process(image).addOnSuccessListener { barcodes ->
            analyzerBorder(barcodes,true)
        }.addOnFailureListener {
            "recognizeQrCode addOnFailureListener barcodes->#${it}".log()
            Toast.makeText(this, "Detection failed", Toast.LENGTH_LONG).show()
        }
    }
}