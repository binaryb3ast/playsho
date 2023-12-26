package com.playsho.android.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;



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
    }

    public static Context getAppContext() {
        return ApplicationLoader.context;
    }

}
