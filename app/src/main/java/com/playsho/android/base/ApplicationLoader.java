package com.playsho.android.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.playsho.android.utils.DimensionUtils;


public class ApplicationLoader extends Application {
    private static final String TAG = "ApplicationLoader";
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @NonNull
    public static String getOriginName() {
        return ApplicationLoader.class.getSimpleName();
    }

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        try {
            DimensionUtils.checkDisplaySize(context, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Context getAppContext() {
        return ApplicationLoader.context;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            DimensionUtils.checkDisplaySize(context, newConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
