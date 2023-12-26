package com.playsho.android.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.gson.Gson;
import com.playsho.android.db.SessionStorage;
import com.playsho.android.utils.DimensionUtils;

/**
 * Custom Application class that initializes the application context, Gson, and SessionStorage.
 */
public class ApplicationLoader extends Application {
    private static final String TAG = "ApplicationLoader";
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private static Gson gson;
    private static SessionStorage sessionStorage;

    /**
     * Gets the name of the ApplicationLoader class.
     *
     * @return The name of the ApplicationLoader class.
     */
    @NonNull
    public static String getOriginName() {
        return ApplicationLoader.class.getSimpleName();
    }

    /**
     * Called when the application is first created. Initializes the application context, Gson, and SessionStorage.
     */
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        gson = new Gson();
        ApplicationLoader.sessionStorage = new SessionStorage();
        try {
            DimensionUtils.checkDisplaySize(context, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the application context.
     *
     * @return The application context.
     */
    public static Context getAppContext() {
        return ApplicationLoader.context;
    }

    /**
     * Called when the configuration of the device changes.
     *
     * @param newConfig The new configuration.
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            DimensionUtils.checkDisplaySize(context, newConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the SessionStorage instance for managing session storage.
     *
     * @return The SessionStorage instance.
     */
    public static SessionStorage getSessionStorage() {
        return ApplicationLoader.sessionStorage;
    }

    /**
     * Gets the Gson instance for JSON serialization and deserialization.
     *
     * @return The Gson instance.
     */
    public static Gson getGson() {
        return ApplicationLoader.gson;
    }

}
