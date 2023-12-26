package com.playsho.android.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.playsho.android.component.ActivityLauncher;
import com.playsho.android.db.SessionStorage;
import com.playsho.android.utils.NetworkListener;
import com.playsho.android.utils.SystemUtilities;

/**
 * A base activity class that provides common functionality and structure for other activities.
 *
 * @param <B> The type of ViewDataBinding used by the activity.
 */
public abstract class BaseActivity<B extends ViewDataBinding> extends AppCompatActivity {

    // Flag to enable or disable the custom back press callback
    private boolean isBackPressCallbackEnable = false;

    /**
     * Abstract method to get the layout resource ID for the activity.
     *
     * @return The layout resource ID.
     */
    protected abstract int getLayoutResourceId();

    /**
     * Abstract method to handle custom behavior on back press.
     */
    protected abstract void onBackPress();

    // View binding instance
    protected B binding;
    protected NetworkListener networkListener;
    // Activity launcher instance
    protected final ActivityLauncher<Intent, ActivityResult> activityLauncher = ActivityLauncher.registerActivityForResult(this);

    // Tag for logging
    protected String TAG = this.getClass().getSimpleName();

    /**
     * Gets a String extra from the Intent.
     *
     * @param key The key of the extra.
     * @return The String extra value.
     */
    protected String getIntentStringExtra(String key) {
        return getIntent().getStringExtra(key);
    }

    /**
     * Gets the name of the current activity.
     *
     * @return The name of the activity.
     */
    protected String getClassName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = DataBindingUtil.setContentView(this, getLayoutResourceId());
        networkListener = new NetworkListener();
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onBackPressedCallback.remove();
    }

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(isBackPressCallbackEnable) {
        @Override
        public void handleOnBackPressed() {
            // Call the abstract method for custom back press handling
            onBackPress();
        }
    };

    /**
     * Sets whether the custom back press callback is enabled or disabled.
     *
     * @param isBackPressCallbackEnable {@code true} to enable the callback, {@code false} to disable it.
     */
    public void setBackPressedCallBackEnable(boolean isBackPressCallbackEnable) {
        this.isBackPressCallbackEnable = isBackPressCallbackEnable;
    }

    /**
     * Sets the status bar color using the SystemUtilities class.
     *
     * @param color The color resource ID.
     * @param isDark {@code true} if the status bar icons should be dark, {@code false} otherwise.
     */
    protected void setStatusBarColor(@ColorRes int color, boolean isDark) {
        SystemUtilities.changeStatusBarBackgroundColor(activity(), color, isDark);
    }

    /**
     * Gets the context of the activity.
     *
     * @return The context of the activity.
     */
    protected Context context() {
        return this;
    }

    /**
     * Gets the activity instance.
     *
     * @return The activity instance.
     */
    protected Activity activity() {
        return this;
    }

    /**
     * Gets the SessionStorage instance from the ApplicationLoader.
     *
     * @return The SessionStorage instance.
     */
    protected SessionStorage getSessionManager() {
        return ApplicationLoader.getSessionStorage();
    }


    /**
     * Checks if the network is available using the NetworkListener.
     *
     * @return {@code true} if the network is available, {@code false} otherwise.
     */
    protected boolean isNetworkAvailable() {
        return networkListener.isNetworkAvailable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerDefaultNetworkCallback(networkListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.unregisterNetworkCallback(networkListener);
    }
}

