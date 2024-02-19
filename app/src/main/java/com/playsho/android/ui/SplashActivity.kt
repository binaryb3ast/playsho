package com.playsho.android.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.playsho.android.R
import com.playsho.android.base.BaseActivity
import com.playsho.android.databinding.ActivitySplashBinding
import com.playsho.android.utils.DimensionUtils
import com.playsho.android.utils.LocalController
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override fun getLayoutResourceId(): Int {
        return R.layout.activity_splash;
    }

    override fun onBackPress() {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(R.color.black_background, true)
        setupLogoWidth()
        binding.txtDescription.typeWrite(this, LocalController.getString(R.string.splash_description) , 33)
        binding.btn.setOnClickListener {
            openActivity<RoomActivity>(
                "code" to "value"
            )
        }
    }

    fun TextView.typeWrite(lifecycleOwner: LifecycleOwner, text: String, intervalMs: Long) {
        this@typeWrite.text = ""
        lifecycleOwner.lifecycleScope.launch {
            repeat(text.length) {
                delay(intervalMs)
                this@typeWrite.text = text.take(it + 1)
            }
        }
    }


    private fun setupLogoWidth() {
        val layoutParams = binding.imgLogo.layoutParams
        val screenWidth = DimensionUtils.getDisplayWidthInPixel()
        val totalSideMargin = DimensionUtils.dpToPx((140 * 2).toFloat())
        val calculatedWidth = screenWidth - totalSideMargin
        layoutParams.width = calculatedWidth
    }



}