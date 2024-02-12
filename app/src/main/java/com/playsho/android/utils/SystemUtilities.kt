package com.playsho.android.utils

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.playsho.android.base.ApplicationLoader

/**
 * Utility class for system-related operations and functionalities.
 */
object SystemUtilities {
    private const val TAG = "SystemUtils"

    /**
     * Changes the color of status bar icons based on the specified mode.
     *
     * @param activity The activity where the status bar icons are changed.
     * @param isDark   True if the status bar icons should be dark, false otherwise.
     */
    fun changeStatusBarIconColor(activity: Activity, isDark: Boolean) {
        val decorView = activity.window.decorView
        var flags = decorView.systemUiVisibility
        if (!isDark) {
            // Make status bar icons dark (e.g., for light background)
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            // Make status bar icons light (e.g., for dark background)
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        decorView.systemUiVisibility = flags
    }

    /**
     * Changes the background color of the status bar and adjusts the icon color.
     *
     * @param activity The activity where the status bar color is changed.
     * @param color    The color resource ID for the status bar.
     * @param isDark   True if the status bar icons should be dark, false otherwise.
     */
    fun changeStatusBarBackgroundColor(activity: Activity, color: Int, isDark: Boolean) {
        changeStatusBarIconColor(activity, isDark)
        activity.window.statusBarColor = ContextCompat.getColor(ApplicationLoader.getAppContext(), color)
    }

    /**
     * Shares plain text through the available sharing options.
     *
     * @param context The context from which the sharing is initiated.
     * @param title   The title of the shared content.
     * @param body    The body or content to be shared.
     */
    fun sharePlainText(context: Context, title: String, body: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, title)
        intent.putExtra(Intent.EXTRA_TEXT, body)
        context.startActivity(Intent.createChooser(intent, title))
    }

    /**
     * Initiates a phone call to the specified phone number.
     *
     * @param context The context from which the phone call is initiated.
     * @param phone   The phone number to call.
     */
    fun callPhoneNumber(context: Context, phone: String) {
        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)))
    }

    /**
     * Vibrates the device for the specified duration.
     *
     * @param milliseconds The duration of the vibration in milliseconds.
     */
    fun doVibrate(milliseconds: Long) {
        val vibrator = ApplicationLoader.getAppContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(milliseconds)
        }
    }

    /**
     * Copies the specified text to the clipboard.
     *
     * @param label The label for the copied text.
     * @param text  The text to be copied.
     */
    fun copyToClipboard(label: String, text: String) {
        val clipboard = ApplicationLoader.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }

    /**
     * Sets the navigation bar to have light icons on a light background.
     *
     * @param window The window for which the navigation bar color is set.
     * @param enable True if the light navigation bar should be enabled, false otherwise.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun setLightNavigationBar(window: Window, enable: Boolean) {
        val decorView = window.decorView
        var flags = decorView.systemUiVisibility
        if (enable) {
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
        decorView.systemUiVisibility = flags
    }

    /**
     * Shows the soft keyboard for the specified view.
     *
     * @param view The view for which the soft keyboard is shown.
     */
    fun showKeyboard(view: View) {
        val inputMethodManager = ApplicationLoader.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    /**
     * Hides the soft keyboard for the specified view.
     *
     * @param view The view for which the soft keyboard is hidden.
     */
    fun hideKeyboard(view: View) {
        val inputMethodManager = ApplicationLoader.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Sets the navigation bar color to white for dialogs on API level 23 and above.
     *
     * @param dialog The dialog for which the navigation bar color is set to white.
     */
    fun setWhiteNavigationBar(@NonNull dialog: Dialog) {
        val window = dialog.window
        window?.let {
            val metrics = DisplayMetrics()
            window.windowManager.defaultDisplay.getMetrics(metrics)

            val dimDrawable = GradientDrawable()

            val navigationBarDrawable = GradientDrawable()
            navigationBarDrawable.shape = GradientDrawable.RECTANGLE
            navigationBarDrawable.setColor(Color.WHITE)

            val layers = arrayOf<Drawable>(dimDrawable, navigationBarDrawable)

            val windowBackground = LayerDrawable(layers)
            windowBackground.setLayerInsetTop(1, metrics.heightPixels)

            window.setBackgroundDrawable(windowBackground)
        }
    }
}
