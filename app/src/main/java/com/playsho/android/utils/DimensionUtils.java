package com.playsho.android.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import com.playsho.android.base.ApplicationLoader;

/**
 * Utility class for handling display dimensions and density conversions.
 */
public class DimensionUtils {

    private static final String TAG = "DimensionUtils";

    // Display properties
    public static Point displaySize = new Point();
    public static boolean firstConfigurationWas;
    public static boolean usingHardwareInput;
    public static DisplayMetrics displayMetrics = new DisplayMetrics();
    public static float screenRefreshRate = 60;
    public static float density = 1;

    /**
     * Retrieves the height of the display in pixels.
     *
     * @return The height of the display.
     */
    public static int getDisplayHeightInPixel() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    /**
     * Retrieves the width of the display in pixels.
     *
     * @return The width of the display.
     */
    public static int getDisplayWidthInPixel() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * Converts density-independent pixels (dp) to pixels (px).
     *
     * @param value The value in dp to be converted.
     * @return The converted value in pixels.
     */
    public static int dpToPx(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(density * value);
    }

    /**
     * Converts pixels (px) to density-independent pixels (dp).
     *
     * @param px The value in pixels to be converted.
     * @return The converted value in dp.
     */
    public static float pxToDp(int px) {
        DisplayMetrics metrics = ApplicationLoader.getAppContext().getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, metrics);
    }

    /**
     * Checks and updates the display size based on the given context and configuration.
     *
     * @param context           The context.
     * @param newConfiguration  The new configuration (can be null).
     */
    public static void checkDisplaySize(Context context, Configuration newConfiguration) {
        try {
            DimensionUtils.density = context.getResources().getDisplayMetrics().density;
            firstConfigurationWas = true;
            Configuration configuration = newConfiguration;
            if (configuration == null) {
                configuration = context.getResources().getConfiguration();
            }
            usingHardwareInput = configuration.keyboard != Configuration.KEYBOARD_NOKEYS && configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO;
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    display.getMetrics(displayMetrics);
                    display.getSize(displaySize);
                    screenRefreshRate = display.getRefreshRate();
                }
            }
            if (configuration.screenWidthDp != Configuration.SCREEN_WIDTH_DP_UNDEFINED) {
                int newSize = (int) Math.ceil(configuration.screenWidthDp * DimensionUtils.density);
                if (Math.abs(displaySize.x - newSize) > 3) {
                    displaySize.x = newSize;
                }
            }
            if (configuration.screenHeightDp != Configuration.SCREEN_HEIGHT_DP_UNDEFINED) {
                int newSize = (int) Math.ceil(configuration.screenHeightDp * DimensionUtils.density);
                if (Math.abs(displaySize.y - newSize) > 3) {
                    displaySize.y = newSize;
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "checkDisplaySize: " );
        }
    }
}
