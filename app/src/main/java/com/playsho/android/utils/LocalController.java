package com.playsho.android.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.playsho.android.base.ApplicationLoader;


/**
 * Utility class for accessing local resources and assets.
 */
public class LocalController {

    /**
     * Retrieves the localized string from the resources.
     *
     * @param resourceId The resource ID of the string.
     * @return The localized string.
     */
    public static String getString(int resourceId) {
        return ApplicationLoader.getAppContext().getResources().getString(resourceId);
    }

    /**
     * Retrieves a custom font from the resources using font resource ID.
     *
     * @param resourceId The resource ID of the font.
     * @return The Typeface object representing the custom font.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Typeface getFont(int resourceId) {
        return ApplicationLoader.getAppContext().getResources().getFont(resourceId);
    }

    /**
     * Retrieves a custom font from the assets folder using the font name.
     *
     * @param fontName The name of the font file.
     * @return The Typeface object representing the custom font.
     */
    public static Typeface getFont(String fontName) {
        try {
            return Typeface.createFromAsset(ApplicationLoader.getAppContext().getAssets(), "fonts/" + fontName + ".ttf");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves the resource ID by name and type.
     *
     * @param name The name of the resource.
     * @param type The type of the resource.
     * @return The resource ID.
     */
    private static int getResourceIdByName(String name, String type) {
        return ApplicationLoader.getAppContext().getResources().getIdentifier(
                name,
                type,
                ApplicationLoader.getAppContext().getPackageName()
        );
    }

    /**
     * Retrieves the color from the resources.
     *
     * @param resource The resource ID of the color.
     * @return The color value.
     */
    public static int getColor(int resource) {
        return ContextCompat.getColor(ApplicationLoader.getAppContext(), resource);
    }

    /**
     * Retrieves the dimension value from the resources.
     *
     * @param resource The resource ID of the dimension.
     * @return The dimension value.
     */
    public static float getDimen(int resource) {
        return ApplicationLoader.getAppContext().getResources().getDimension(resource);
    }

    /**
     * Retrieves the dimension pixel size from the resources.
     *
     * @param resource The resource ID of the dimension pixel size.
     * @return The dimension pixel size.
     */
    public static float getDimensionPixelSize(int resource) {
        return ApplicationLoader.getAppContext().getResources().getDimensionPixelSize(resource);
    }

    /**
     * Retrieves the drawable from the resources.
     *
     * @param resource The resource ID of the drawable.
     * @return The Drawable object.
     */
    public static Drawable getDrawable(int resource) {
        return ContextCompat.getDrawable(ApplicationLoader.getAppContext(), resource);
    }
}

