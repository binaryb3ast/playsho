package com.playsho.android.utils

import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.playsho.android.base.ApplicationLoader

/**
 * Utility class for accessing local resources and assets.
 */
object LocalController {

    /**
     * Retrieves the localized string from the resources.
     *
     * @param resourceId The resource ID of the string.
     * @return The localized string.
     */
    fun getString(resourceId: Int): String {
        return ApplicationLoader.getAppContext().resources.getString(resourceId)
    }

    /**
     * Retrieves a custom font from the resources using font resource ID.
     *
     * @param resourceId The resource ID of the font.
     * @return The Typeface object representing the custom font.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun getFont(resourceId: Int): Typeface {
        return ApplicationLoader.getAppContext().resources.getFont(resourceId)
    }

    /**
     * Retrieves a custom font from the assets folder using the font name.
     *
     * @param fontName The name of the font file.
     * @return The Typeface object representing the custom font.
     */
    fun getFont(fontName: String): Typeface? {
        return try {
            Typeface.createFromAsset(ApplicationLoader.getAppContext().assets, "fonts/$fontName.ttf")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Retrieves the color from the resources.
     *
     * @param resource The resource ID of the color.
     * @return The color value.
     */
    fun getColor(resource: Int): Int {
        return ContextCompat.getColor(ApplicationLoader.getAppContext(), resource)
    }

    /**
     * Retrieves the dimension value from the resources.
     *
     * @param resource The resource ID of the dimension.
     * @return The dimension value.
     */
    fun getDimen(resource: Int): Float {
        return ApplicationLoader.getAppContext().resources.getDimension(resource)
    }

    /**
     * Retrieves the dimension pixel size from the resources.
     *
     * @param resource The resource ID of the dimension pixel size.
     * @return The dimension pixel size.
     */
    fun getDimensionPixelSize(resource: Int): Int {
        return ApplicationLoader.getAppContext().resources.getDimensionPixelSize(resource)
    }

    /**
     * Retrieves the drawable from the resources.
     *
     * @param resource The resource ID of the drawable.
     * @return The Drawable object.
     */
    fun getDrawable(resource: Int): Drawable? {
        return ContextCompat.getDrawable(ApplicationLoader.getAppContext(), resource)
    }
}
