package com.playsho.android.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.playsho.android.base.ApplicationLoader
import com.playsho.android.data.Device
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * Utility class for retrieving information about the device and application.
 */
object DeviceUtils {

    /**
     * Operating System identifier for Android.
     */
    const val OS = "android"

    /**
     * TelephonyManager instance for network-related information
     */
    private val telephonyManager: TelephonyManager? by lazy {
        ApplicationLoader.context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
    }

    /**
     * Retrieves the version name of the application.
     *
     * @return The version name of the application.
     */
    fun getAppVersionName(): String {
        val packageManager = ApplicationLoader.context.packageManager
        val packageName = ApplicationLoader.context.packageName
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "ERR"
        }
    }

    fun getAppVersionCode(): Int {
        val packageManager = ApplicationLoader.context.packageManager
        val packageName = ApplicationLoader.context.packageName
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                packageInfo.versionCode
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            -1 // Return -1 as an error code
        }
    }

    /**
     * Retrieves the first installation time of the application.
     *
     * @return The first installation time of the application.
     */
    fun getFirstInstallTime(): Long {
        val packageManager = ApplicationLoader.context.packageManager
        val packageName = ApplicationLoader.context.packageName
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            packageInfo.firstInstallTime
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            0
        }
    }

    /**
     * Retrieves the last update time of the application.
     *
     * @return The last update time of the application.
     */
    fun getLastUpdateTime(): Long {
        val packageManager = ApplicationLoader.context.packageManager
        val packageName = ApplicationLoader.context.packageName
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            packageInfo.lastUpdateTime
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            0
        }
    }

    /**
     * Retrieves the network operator name.
     *
     * @return The network operator name.
     */
    fun getNetworkOperatorName(): String {
        return telephonyManager?.networkOperatorName ?: ""
    }

    /**
     * Retrieves the percentage of free memory on the device.
     *
     * @return The percentage of free memory.
     */
    fun getFreeMemoryPercentage(): Int {
        val activityManager = ApplicationLoader.context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return ((memoryInfo.availMem.toDouble() / memoryInfo.totalMem) * 100.0).toInt()
    }

    fun getSimCountryIso(): String {
        return telephonyManager?.simCountryIso ?: ""
    }

    fun getSimOperator(): String {
        return telephonyManager?.simOperator ?: ""
    }

    fun getBatteryPercentage(): Int {
        val batteryManager = ApplicationLoader.context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    fun getNetworkClass(): String {
        val cm = ApplicationLoader.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        if (info == null || !info.isConnectedOrConnecting)
            return "NOT_CONNECTED" // not connected
        return when (info.type) {
            ConnectivityManager.TYPE_WIFI -> "WIFI"
            ConnectivityManager.TYPE_MOBILE -> {
                when (info.subtype) {
                    TelephonyManager.NETWORK_TYPE_GPRS,
                    TelephonyManager.NETWORK_TYPE_EDGE,
                    TelephonyManager.NETWORK_TYPE_CDMA,
                    TelephonyManager.NETWORK_TYPE_1xRTT,
                    TelephonyManager.NETWORK_TYPE_IDEN,
                    TelephonyManager.NETWORK_TYPE_GSM -> "2G"
                    TelephonyManager.NETWORK_TYPE_UMTS,
                    TelephonyManager.NETWORK_TYPE_EVDO_0,
                    TelephonyManager.NETWORK_TYPE_EVDO_A,
                    TelephonyManager.NETWORK_TYPE_HSDPA,
                    TelephonyManager.NETWORK_TYPE_HSUPA,
                    TelephonyManager.NETWORK_TYPE_HSPA,
                    TelephonyManager.NETWORK_TYPE_EVDO_B,
                    TelephonyManager.NETWORK_TYPE_EHRPD,
                    TelephonyManager.NETWORK_TYPE_HSPAP,
                    TelephonyManager.NETWORK_TYPE_TD_SCDMA -> "3G"
                    TelephonyManager.NETWORK_TYPE_LTE,
                    TelephonyManager.NETWORK_TYPE_IWLAN,
                    19 -> "4G" // LTE_CA
                    TelephonyManager.NETWORK_TYPE_NR -> "5G"
                    else -> "?"
                }
            }
            else -> "?"
        }
    }

    fun isAirplaneModeOn(): Boolean {
        return Settings.Global.getInt(
            ApplicationLoader.context.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON, 0
        ) != 0
    }

    fun getModel(): String {
        return Build.MODEL
    }

    fun getManufacturer(): String {
        return Build.MANUFACTURER
    }

    fun getBrand(): String {
        return Build.BRAND
    }


    fun getOsVersion(): String {
        return Build.VERSION.RELEASE
    }

    fun createDevice():Device{
        return Device(
            brand = this.getBrand().lowercase(),
            os = "android",
            model =  this.getModel(),
            secret = this.getDeviceUniqueId(),
            manufacturer = this.getManufacturer(),
            osVersion = this.getOsVersion(),
            appVersionName = this.getAppVersionName(),
            appVersion = this.getAppVersionCode(),
            lastUpdateAt = this.getLastUpdateTime(),
            firstInstallAt = this.getFirstInstallTime(),
        )
    }

    @SuppressLint("HardwareIds")
    fun getDeviceUniqueId(): String {
        val androidId = Settings.Secure.getString(
            ApplicationLoader.context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: UUID.randomUUID().toString()
        val fingerprint = Build.FINGERPRINT
        return "$fingerprint/$androidId"
    }

}