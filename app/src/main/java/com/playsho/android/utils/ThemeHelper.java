package com.playsho.android.utils;


import android.graphics.drawable.GradientDrawable;

import androidx.annotation.ColorRes;


/**
 * Helper class for creating GradientDrawable objects with various shapes and styles.
 */
public class ThemeHelper {

    /**
     * Creates a rectangular GradientDrawable with the specified color and radius.
     *
     * @param color  The color resource ID for the rectangle.
     * @param radius The corner radius in dp for the rectangle.
     * @return A rectangular GradientDrawable.
     */
    public static GradientDrawable createRect(@ColorRes int color, int radius) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadius( DimensionUtils.dpToPx(radius));
        gradientDrawable.setColor(LocalController.getColor(color));
        return gradientDrawable;
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
    public static GradientDrawable createRect(@ColorRes int color, int radius, @ColorRes int strokeColor, int strokeWidth) {
        GradientDrawable gradientDrawable = createRect(color, radius);
        gradientDrawable.setStroke(
                (int) DimensionUtils.dpToPx(strokeWidth),
                LocalController.getColor(strokeColor)
        );
        return gradientDrawable;
    }

    /**
     * Creates a circular GradientDrawable with the specified color.
     *
     * @param color The color resource ID for the circle.
     * @return A circular GradientDrawable.
     */
    public static GradientDrawable createCircle(@ColorRes int color) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.OVAL);
        gradientDrawable.setColor(LocalController.getColor(color));
        return gradientDrawable;
    }

    /**
     * Creates a circular GradientDrawable with the specified color, stroke color, and stroke width.
     *
     * @param color       The color resource ID for the circle.
     * @param strokeColor The stroke color resource ID for the circle.
     * @param strokeWidth The stroke width in dp for the circle.
     * @return A circular GradientDrawable with stroke.
     */
    public static GradientDrawable createCircle(@ColorRes int color, @ColorRes int strokeColor, int strokeWidth) {
        GradientDrawable gradientDrawable = createCircle(color);
        gradientDrawable.setStroke(
                (int) DimensionUtils.dpToPx(strokeWidth),
                LocalController.getColor(strokeColor)
        );
        return gradientDrawable;
    }

}