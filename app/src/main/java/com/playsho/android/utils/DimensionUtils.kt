package com.playsho.android.utils
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.WindowManager
import com.playsho.android.base.ApplicationLoader
import kotlin.math.abs
import kotlin.math.ceil

/**
 * Utility class for handling display dimensions and density conversions.
 */
object DimensionUtils {

    private const val TAG = "DimensionUtils"

    // Display properties
    var displaySize = Point()
    var firstConfigurationWas: Boolean = false
    var usingHardwareInput: Boolean = false
    var displayMetrics = DisplayMetrics()
    var screenRefreshRate: Float = 60f
    var density: Float = 1f

    /**
     * Retrieves the height of the display in pixels.
     *
     * @return The height of the display.
     */
    fun getDisplayHeightInPixel(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

    /**
     * Retrieves the width of the display in pixels.
     *
     * @return The width of the display.
     */
    fun getDisplayWidthInPixel(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    /**
     * Converts density-independent pixels (dp) to pixels (px).
     *
     * @param value The value in dp to be converted.
     * @return The converted value in pixels.
     */
    fun dpToPx(value: Float): Int {
        if (value == 0f) {
            return 0
        }
        return ceil((density * value).toDouble()).toInt()
    }

    /**
     * Converts pixels (px) to density-independent pixels (dp).
     *
     * @param px The value in pixels to be converted.
     * @return The converted value in dp.
     */
    fun pxToDp(px: Int): Float {
        val metrics = ApplicationLoader.getAppContext().resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px.toFloat(), metrics)
    }

    /**
     * Checks and updates the display size based on the given context and configuration.
     *
     * @param context The context.
     * @param newConfiguration The new configuration (can be null).
     */
    fun checkDisplaySize(context: Context, newConfiguration: Configuration?) {
        try {
            density = context.resources.displayMetrics.density
            firstConfigurationWas = true
            val configuration = newConfiguration ?: context.resources.configuration
            usingHardwareInput = configuration.keyboard != Configuration.KEYBOARD_NOKEYS && configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO
            val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
            manager?.defaultDisplay?.let { display ->
                display.getMetrics(displayMetrics)
                display.getSize(displaySize)
                screenRefreshRate = display.refreshRate
            }
            if (configuration.screenWidthDp != Configuration.SCREEN_WIDTH_DP_UNDEFINED) {
                val newSize = (configuration.screenWidthDp * density + 0.5f).toInt()
                if (abs(displaySize.x - newSize) > 3) {
                    displaySize.x = newSize
                }
            }
            if (configuration.screenHeightDp != Configuration.SCREEN_HEIGHT_DP_UNDEFINED) {
                val newSize = (configuration.screenHeightDp * density + 0.5f).toInt()
                if (abs(displaySize.y - newSize) > 3) {
                    displaySize.y = newSize
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "checkDisplaySize: ", e)
        }
    }
}