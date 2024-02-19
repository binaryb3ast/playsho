package com.playsho.android.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.playsho.android.component.ActivityLauncher
import com.playsho.android.db.SessionStorage
import com.playsho.android.utils.NetworkListener
import com.playsho.android.utils.SystemUtilities
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A base activity class that provides common functionality and structure for other activities.
 *
 * @param B The type of ViewDataBinding used by the activity.
 */
abstract class BaseActivity<B : ViewDataBinding> : AppCompatActivity() {

    // Flag to enable or disable the custom back press callback
    private var isBackPressCallbackEnable = false

    // View binding instance
    protected lateinit var binding: B
    protected lateinit var networkListener: NetworkListener
    // Activity launcher instance
    protected val activityLauncher = ActivityLauncher.registerActivityForResult(this)

    // Tag for logging
    protected open val TAG: String = javaClass.simpleName

    /**
     * Abstract method to get the layout resource ID for the activity.
     *
     * @return The layout resource ID.
     */
    protected abstract fun getLayoutResourceId(): Int

    /**
     * Abstract method to handle custom behavior on back press.
     */
    protected abstract fun onBackPress()

    /**
     * Gets a String extra from the Intent.
     *
     * @param key The key of the extra.
     * @return The String extra value.
     */
    protected fun getIntentStringExtra(key: String): String? {
        return intent.getStringExtra(key)
    }

    /**
     * Gets the name of the current activity.
     *
     * @return The name of the activity.
     */
    protected fun getClassName(): String {
        return javaClass.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = DataBindingUtil.setContentView(this, getLayoutResourceId())
        networkListener = NetworkListener()
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback.remove()
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(isBackPressCallbackEnable) {
        override fun handleOnBackPressed() {
            // Call the abstract method for custom back press handling
            onBackPress()
        }
    }

    /**
     * Sets whether the custom back press callback is enabled or disabled.
     *
     * @param isBackPressCallbackEnable true to enable the callback, false to disable it.
     */
    fun setBackPressedCallBackEnable(isBackPressCallbackEnable: Boolean) {
        this.isBackPressCallbackEnable = isBackPressCallbackEnable
    }

    /**
     * Sets the status bar color using the SystemUtilities class.
     *
     * @param color The color resource ID.
     * @param isDark true if the status bar icons should be dark, false otherwise.
     */
    protected fun setStatusBarColor(@ColorRes color: Int, isDark: Boolean) {
        SystemUtilities.changeStatusBarBackgroundColor(activity(), color, isDark)
    }

    /**
     * Gets the context of the activity.
     *
     * @return The context of the activity.
     */
    protected fun context(): Context {
        return this
    }

    /**
     * Gets the activity instance.
     *
     * @return The activity instance.
     */
    protected fun activity(): Activity {
        return this
    }

    /**
     * Gets the SessionStorage instance from the ApplicationLoader.
     *
     * @return The SessionStorage instance.
     */
    protected fun getSessionManager(): SessionStorage {
        return ApplicationLoader.getSessionStorage()
    }

    /**
     * Checks if the network is available using the NetworkListener.
     *
     * @return true if the network is available, false otherwise.
     */
    protected fun isNetworkAvailable(): AtomicBoolean {
        return networkListener.isNetworkAvailable
    }

    override fun onResume() {
        super.onResume()
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(networkListener)
    }

    override fun onPause() {
        super.onPause()
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(networkListener)
    }

    protected inline fun <reified T : Activity> Activity.openActivity(vararg params: Pair<String, Any?>) {
        val intent = createIntent<T>().apply {
            for (param in params) {
                val (key, value) = param
                when (value) {
                    is String -> putExtra(key, value)
                    is Int -> putExtra(key, value)
                    // Add more cases for other data types as needed
                }
            }
        }
        startActivity(intent)
    }

    protected inline fun <reified T: Activity> Context.createIntent() =
        Intent(this, T::class.java)
}
