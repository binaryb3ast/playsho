package com.playsho.android.base

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson
import com.playsho.android.db.SessionStorage
import com.playsho.android.utils.DimensionUtils

/**
 * Custom Application class that initializes the application context, Gson, and SessionStorage.
 */
class ApplicationLoader : Application() {

    companion object {
          lateinit var context: Context
        private lateinit var gson: Gson
        private lateinit var sessionStorage: SessionStorage


        /**
         * Gets the SessionStorage instance for managing session storage.
         *
         * @return The SessionStorage instance.
         */
        @JvmStatic
        fun getSessionStorage(): SessionStorage {
            return sessionStorage
        }
    }

    /**
     * Called when the application is first created. Initializes the application context, Gson, and SessionStorage.
     */
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        gson = Gson()
        sessionStorage = SessionStorage()
        try {
            DimensionUtils.checkDisplaySize(context, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Called when the configuration of the device changes.
     *
     * @param newConfig The new configuration.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        try {
            DimensionUtils.checkDisplaySize(context, newConfig)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}