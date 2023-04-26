package com.qrtb.qrred.ac

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.mlkit.vision.barcode.common.Barcode
import com.qrtb.qrred.R
import com.qrtb.qrred.base.BaseActivity
import com.qrtb.qrred.common.jumpEmailApp
import com.qrtb.qrred.common.shareTextToOtherApp
import com.qrtb.qrred.databinding.AcScanResultBinding
import kotlin.reflect.typeOf

/**
 * Date：2023/4/24
 * Describe:
 */
class ScanResultActivity : BaseActivity<AcScanResultBinding>(R.layout.ac_scan_result) {

    override fun onInitView() {
        val result = intent.getStringExtra("result")
        val type = intent.getIntExtra("type", Barcode.TYPE_TEXT)
        binding.run {
            ivBack.setOnClickListener {
                onBackPressed()
            }
            ivCopy.setOnClickListener {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(null, result)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(
                    this@ScanResultActivity, "Copy successful!", Toast.LENGTH_SHORT
                ).show()
            }
            ivShare.setOnClickListener {
                result?.let {
                    shareTextToOtherApp(it)
                }
            }
            ivSearch.setOnClickListener {
                result?.let {
                    toSearch(type, it)
                }
            }
            tvType.text = getTypeText(type)
            tvContent.text = result
        }
    }

    private fun getTypeText(type: Int): String {
        return when (type) {
            Barcode.TYPE_TEXT -> "TEXT"
            Barcode.TYPE_URL -> "URL"
            Barcode.TYPE_WIFI -> "Wi-Fi"
            else -> "TEXT"
        }
    }

    private val SEARCH_URL = "https://www.google.com/search?q=%s"
    private fun toSearch(type: Int, result: String) {
        runCatching {
            Intent(Intent.ACTION_VIEW).apply {
                data = if (type == Barcode.TYPE_URL) {
                    Uri.parse(result)
                } else {
                    Uri.parse(String.format(SEARCH_URL, result))
                }
                startActivity(this)
            }
        }
    }
}