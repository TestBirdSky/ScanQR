package com.qrtb.qrred.ac

import androidx.lifecycle.lifecycleScope
import com.qrtb.qrred.MainActivity
import com.qrtb.qrred.R
import com.qrtb.qrred.base.BaseActivity
import com.qrtb.qrred.common.toActivity
import com.qrtb.qrred.databinding.ActivityLauncherBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * Date：2023/4/24
 * Describe:
 */
class LauncherActivity : BaseActivity<ActivityLauncherBinding>(R.layout.activity_launcher) {
    private val TIME_ALL = 3000L
    private var isFinishAnimator = false
    override fun onInitView() {
        animator()
    }

    private fun animator() {
        lifecycleScope.launch(Dispatchers.IO) {
            flowStart().collect {
                binding.progress.progress = it.toFloat()
                if (it >= 100) {
                    jumpMain()
                }
            }
        }

    }

    private fun flowStart(): Flow<Int> {
        return flow {
            val time = System.currentTimeMillis()
            val period = TIME_ALL / 100
            var count = 0
            while (System.currentTimeMillis() - time < TIME_ALL && !isFinishAnimator) {
                emit(count++)
                delay(period)
            }
            emit(100)
        }.flowOn(Dispatchers.Main)
    }

    private fun jumpMain(){
        toActivity<MainActivity>()
        finish()
    }

    override fun onBackPressed() {

    }

}