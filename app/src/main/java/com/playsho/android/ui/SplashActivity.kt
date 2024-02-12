package com.playsho.android.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.playsho.android.R
import com.playsho.android.base.BaseActivity
import com.playsho.android.databinding.ActivitySplashBinding
import com.playsho.android.utils.DimensionUtils

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
        binding.imgLogo.animate()
            .translationY(calculateFinalYPosition().toFloat())
            .setDuration(1000) // Adjust the duration as needed
            .setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.accelerate_decelerate))
            .start()
    }

    private fun calculateFinalYPosition(): Int {
        // You need to calculate the Y position where you want the ImageView to end up.
        // For example, if you want it to be at 50% of the screen height, you can calculate it like this:
        val screenHeight = resources.displayMetrics.heightPixels
        return (screenHeight * 0.5).toInt()
    }

    private fun setupLogoWidth() {
        val layoutParams = binding.imgLogo.layoutParams
        val screenWidth = DimensionUtils.getDisplayWidthInPixel()
        val totalSideMargin = DimensionUtils.dpToPx((140 * 2).toFloat())
        val calculatedWidth = screenWidth - totalSideMargin
        layoutParams.width = calculatedWidth
    }


}