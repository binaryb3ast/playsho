package com.playsho.android.base

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.transition.Transition
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * BasePopup is an abstract class that provides a foundation for creating custom popup windows in Android applications.
 * It extends the PopupWindow class and includes common functionalities for handling popups.
 * To use this class, you need to extend it and implement the required methods.
 * Usage Example:
 * ```kotlin
 * class CustomPopup : BasePopup<YourBindingClass>() {
 *
 *     override fun getLayoutResourceId(): Int {
 *         // Return the layout resource ID for your custom popup layout
 *         return R.layout.your_popup_layout
 *     }
 *
 *     override fun onViewInitialized() {
 *         // Initialize your views and set any necessary listeners
 *         // This method is called after inflating the view and before showing the popup
 *     }
 * }
 * ```
 * Features:
 * - Supports data binding with ViewDataBinding.
 * - Provides methods for showing the popup at a specified location or at the center of an anchor view.
 * - Allows customization of enter transition.
 *
 * @param T The type of ViewDataBinding used in the popup.
 */
abstract class BasePopup<T : ViewDataBinding>(context: Context) : PopupWindow(context) {

    /**
     * The ViewDataBinding instance associated with the popup layout.
     */
    protected lateinit var binding: T

    /**
     * Inflates the popup view and performs necessary setup.
     */
    private fun inflateView(context: Context) {
        val inflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(inflater, getLayoutResourceId(), null, false)
        setContentView(binding.root)
        setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true

        // Custom initialization for the view
        onViewInitialized()
    }


    /**
     * Abstract method to get the layout resource ID for the popup.
     *
     * @return The layout resource ID.
     */
    protected abstract fun getLayoutResourceId(): Int

    /**
     * Abstract method called after inflating the view and before showing the popup.
     * Use this method to initialize views and set any necessary listeners.
     */
    protected abstract fun onViewInitialized()

    /**
     * Shows the popup at the specified location, triggered by a MotionEvent.
     *
     * @param anchor The anchor view.
     * @param event  The motion event that triggered the popup.
     */
    fun showAtLocation(anchor: View, event: MotionEvent) {
        // Implementation for showing the popup at a specific location
    }

    /**
     * Shows the popup at the center of the anchor view.
     *
     * @param anchor The anchor view.
     */
    fun showAtLocation(anchor: View) {
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)
        val x = location[0] + anchor.width / 2
        val y = location[1] + anchor.height / 2
        showAtLocation(anchor, 0, x, y)
    }

    init {
        inflateView(context)
    }
}