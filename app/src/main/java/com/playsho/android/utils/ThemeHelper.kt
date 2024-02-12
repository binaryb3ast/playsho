package com.playsho.android.utils

import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.playsho.android.base.ApplicationLoader

/**
 * Helper class for creating GradientDrawable objects with various shapes and styles.
 */
object ThemeHelper {

    /**
     * Creates a rectangular GradientDrawable with the specified color and radius.
     *
     * @param color  The color resource ID for the rectangle.
     * @param radius The corner radius in dp for the rectangle.
     * @return A rectangular GradientDrawable.
     */
    fun createRect(@ColorRes color: Int, radius: Int): GradientDrawable {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        gradientDrawable.cornerRadius = DimensionUtils.dpToPx(radius.toFloat()).toFloat()
        gradientDrawable.setColor(ContextCompat.getColor(ApplicationLoader.getAppContext(), color))
        return gradientDrawable
    }

    /**
     * Creates a rectangular GradientDrawable with the specified color, radius, stroke color, and stroke width.
     *
     * @param color       The color resource ID for the rectangle.
     * @param radius      The corner radius in dp for the rectangle.
     * @param strokeColor The stroke color resource ID for the rectangle.
     * @param strokeWidth The stroke width in dp for the rectangle.
     * @return A rectangular GradientDrawable with stroke.
     */
    fun createRect(@ColorRes color: Int, radius: Int, @ColorRes strokeColor: Int, strokeWidth: Int): GradientDrawable {
        val gradientDrawable = createRect(color, radius)
        gradientDrawable.setStroke(
            DimensionUtils.dpToPx(strokeWidth.toFloat()).toInt(),
            ContextCompat.getColor(ApplicationLoader.getAppContext(), strokeColor)
        )
        return gradientDrawable
    }

    /**
     * Creates a circular GradientDrawable with the specified color.
     *
     * @param color The color resource ID for the circle.
     * @return A circular GradientDrawable.
     */
    fun createCircle(@ColorRes color: Int): GradientDrawable {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.shape = GradientDrawable.OVAL
        gradientDrawable.setColor(ContextCompat.getColor(ApplicationLoader.getAppContext(), color))
        return gradientDrawable
    }

    /**
     * Creates a circular GradientDrawable with the specified color, stroke color, and stroke width.
     *
     * @param color       The color resource ID for the circle.
     * @param strokeColor The stroke color resource ID for the circle.
     * @param strokeWidth The stroke width in dp for the circle.
     * @return A circular GradientDrawable with stroke.
     */
    fun createCircle(@ColorRes color: Int, @ColorRes strokeColor: Int, strokeWidth: Int): GradientDrawable {
        val gradientDrawable = createCircle(color)
        gradientDrawable.setStroke(
            DimensionUtils.dpToPx(strokeWidth.toFloat()).toInt(),
            ContextCompat.getColor(ApplicationLoader.getAppContext(), strokeColor)
        )
        return gradientDrawable
    }
}
