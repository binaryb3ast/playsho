package com.playsho.android.utils;

import static android.content.Context.TELEPHONY_SERVICE;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.material.appbar.AppBarLayout;
import com.playsho.android.base.ApplicationLoader;

import java.util.List;
import java.util.Objects;


/**
 * Utility class for system-related operations and functionalities.
 */
public class SystemUtilities {
    /**
     * Tag for logging purposes.
     */
    private static final String TAG = "SystemUtils";

    /**
     * Changes the color of status bar icons based on the specified mode.
     *
     * @param activity The activity where the status bar icons are changed.
     * @param isDark   True if the status bar icons should be dark, false otherwise.
     */
    public static void changeStatusBarIconColor(Activity activity, boolean isDark) {
        View decorView = activity.getWindow().getDecorView();
        int flags = decorView.getSystemUiVisibility();
        if (!isDark) {
            // Make status bar icons dark (e.g., for light background)
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            // Make status bar icons light (e.g., for dark background)
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        decorView.setSystemUiVisibility(flags);
    }

    /**
     * Changes the background color of the status bar and adjusts the icon color.
     *
     * @param activity The activity where the status bar color is changed.
     * @param color    The color resource ID for the status bar.
     * @param isDark   True if the status bar icons should be dark, false otherwise.
     */
    public static void changeStatusBarBackgroundColor(Activity activity, int color, boolean isDark) {
        changeStatusBarIconColor(activity, isDark);
        activity.getWindow().setStatusBarColor(LocalController.getColor(color));
    }

    /**
     * Shares plain text through the available sharing options.
     *
     * @param context The context from which the sharing is initiated.
     * @param title   The title of the shared content.
     * @param body    The body or content to be shared.
     */
    public static void sharePlainText(Context context, String title, String body) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, title));
    }

    /**
     * Initiates a phone call to the specified phone number.
     *
     * @param context The context from which the phone call is initiated.
     * @param phone   The phone number to call.
     */
    public static void callPhoneNumber(Context context, String phone) {
        context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)));
    }

    /**
     * Vibrates the device for the specified duration.
     *
     * @param milisecond The duration of the vibration in milliseconds.
     */
    public static void doVibrate(int milisecond) {
        Vibrator v = (Vibrator) ApplicationLoader.getAppContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Objects.requireNonNull(v).vibrate(VibrationEffect.createOneShot(milisecond, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            Objects.requireNonNull(v).vibrate(milisecond);
        }
    }

    /**
     * Copies the specified text to the clipboard.
     *
     * @param label The label for the copied text.
     * @param text  The text to be copied.
     */
    public static void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }

    /**
     * Sets the navigation bar to have light icons on a light background.
     *
     * @param window The window for which the navigation bar color is set.
     * @param enable True if the light navigation bar should be enabled, false otherwise.
     */
    public static void setLightNavigationBar(Window window, boolean enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final View decorView = window.getDecorView();
            int flags = decorView.getSystemUiVisibility();
            if (enable) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
            decorView.setSystemUiVisibility(flags);
        }
    }

    /**
     * Shows the soft keyboard for the specified view.
     *
     * @param view The view for which the soft keyboard is shown.
     */
    public static void showKeyboard(View view) {
        if (view == null) {
            return;
        }
        try {
            InputMethodManager inputManager = (InputMethodManager) ApplicationLoader.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception ignored) {
        }
    }

    /**
     * Hides the soft keyboard for the specified view.
     *
     * @param view The view for which the soft keyboard is hidden.
     */
    public static void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        try {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!imm.isActive()) {
                return;
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            Log.e(TAG, "hideKeyboard: ", e);
        }
    }

    /**
     * Sets the navigation bar color to white for dialogs on API level 23 and above.
     *
     * @param dialog The dialog for which the navigation bar color is set to white.
     */
    public static void setWhiteNavigationBar(@NonNull Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            GradientDrawable dimDrawable = new GradientDrawable();

            GradientDrawable navigationBarDrawable = new GradientDrawable();
            navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
            navigationBarDrawable.setColor(Color.WHITE);

            Drawable[] layers = {dimDrawable, navigationBarDrawable};

            LayerDrawable windowBackground = new LayerDrawable(layers);
            windowBackground.setLayerInsetTop(1, metrics.heightPixels);

            window.setBackgroundDrawable(windowBackground);
        }
    }
}