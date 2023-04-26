package com.qrtb.qrred.ac

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.qrtb.qrred.AppQ
import com.qrtb.qrred.R
import com.qrtb.qrred.base.BaseActivity
import com.qrtb.qrred.common.*
import com.qrtb.qrred.databinding.AcCreateQrBinding
import kotlinx.coroutines.launch

/**
 * Date：2023/4/24
 * Describe:
 */
class CreateActivity : BaseActivity<AcCreateQrBinding>(R.layout.ac_create_qr) {
    private var mBitmap: Bitmap? = null
    override fun onInitView() {
        binding.run {
            ivBack.setOnClickListener {
                onBackPressed()
            }
            tvCreate.setOnClickListener {
                val input = etInput.text.toString()
                createQrCode(input)
                hideSoft(etInput)
                showCreateResult()
            }
            layoutDownload.setOnClickListener {
                checkReadMediaPermission {
                    mBitmap?.let {
                        saveBitmapToMedia(it) {
                            Toast.makeText(
                                this@CreateActivity,
                                getString(R.string.save_to_album),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
            layoutShare.setOnClickListener {
                checkReadMediaPermission {
                    mBitmap?.let {
                        shareBitmapToOtherApp(it)
                    }
                }
            }
            etInput.addTextChangedListener {
                if (it?.isNotEmpty() == true) {
                    tvCreate.visibility = View.VISIBLE
                } else {
                    tvCreate.visibility = View.GONE
                }
            }
        }
    }

    private fun showCreateResult() {
        binding.run {
            ivBg.visibility = View.GONE
            tvCreate.visibility = View.GONE
            etInput.visibility = View.GONE

            ivBg2.visibility = View.VISIBLE
            layoutShare.visibility = View.VISIBLE
            layoutDownload.visibility = View.VISIBLE
        }
    }

    private fun createQrCode(content: String) {
        lifecycleScope.launch {
            mBitmap = QrCodeHelper.generateQRCode(content, dpToPx(200), dpToPx(200))
            binding.ivQrCode.setImageBitmap(mBitmap)
            showCreateResult()
        }
    }

    private fun hideSoft(view: View) {
        val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}