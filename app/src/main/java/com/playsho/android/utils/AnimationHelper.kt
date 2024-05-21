package com.playsho.android.utils

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import com.playsho.android.base.ApplicationLoader

object AnimationHelper {

    object Duration {
        const val DISAPPEAR_MESSAGE = 5000L
        const val SLIDE_IN_FROM_BOTTOM = 400L
    }

    fun slideInFromBottom(view: View, duration: Long) {
        val animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        animation.duration = duration
        view.startAnimation(animation)
    }

    fun fadeIn(view: View, duration: Long) {
        val animation = AlphaAnimation(0.0f, 1.0f)
        animation.duration = duration
        view.startAnimation(animation)
    }

    fun fadeOut(view: View, duration: Long) {
        val animation = AlphaAnimation(1.0f, 0.0f)
        animation.duration = duration
        view.startAnimation(animation)
    }

}