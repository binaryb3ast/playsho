package com.playsho.android.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;



public class LocalController {

    public static String getString(int resourceId) {
        return ApplicationLoader.getAppContext().getResources().getString(resourceId);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Typeface getFont(int resourceId) {
        return ApplicationLoader.getAppContext().getResources().getFont(resourceId);
    }

    public static Typeface getFont(String fontName) {
        try {
           return Typeface.createFromAsset(ApplicationLoader.getAppContext().getAssets(), "fonts/" + fontName + ".ttf");
            // Use the typeface as needed
        } catch (Exception e) {
            e.printStackTrace();
            return null;
            // Handle the exception, log or display an error message
        }
    }

    private static int getResourceIdByName(String name,String type) {
        return ApplicationLoader.getAppContext().getResources().getIdentifier(
                name,
                type,
                ApplicationLoader.getAppContext().getPackageName()
        );
    }


    public static int getColor(int resource) {        Context context = ApplicationLoader.getAppContext();
        return ContextCompat.getColor(ApplicationLoader.getAppContext(), resource);
    }

    public static float getDimen(int resource) {
        return ApplicationLoader.getAppContext().getResources().getDimension(resource);
    }


    public static float getDimensionPixelSize(int resource) {
        return ApplicationLoader.getAppContext().getResources().getDimensionPixelSize(resource);
    }

    public static Drawable getDrawable(int resource) {
        return ContextCompat.getDrawable(ApplicationLoader.getAppContext(), resource);
    }

}
