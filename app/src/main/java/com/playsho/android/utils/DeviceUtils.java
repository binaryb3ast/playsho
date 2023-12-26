package com.playsho.android.utils;

import static android.content.Context.TELEPHONY_SERVICE;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Nullable;


import com.playsho.android.base.ApplicationLoader;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * Utility class for retrieving information about the device and application.
 */
public class DeviceUtils {

    /**
     * Number of hashed bytes used in generating app hash.
     */
    public static final int NUM_HASHED_BYTES = 9;

    /**
     * Number of base64 characters used in generating app hash.
     */
    public static final int NUM_BASE64_CHAR = 11;

    /**
     * Hash type for SHA-256 algorithm.
     */
    private static final String HASH_TYPE_SHA_256 = "SHA-256";

    /**
     * Operating System identifier for Android.
     */
    public static final String OS = "android";

    /**
     * TelephonyManager instance for network-related information
     */
    private static final TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.getAppContext().getSystemService(TELEPHONY_SERVICE);

    /**
     * Retrieves the version name of the application.
     *
     * @return The version name of the application.
     */
    public static String getAppVersionName() {
        String version;
        PackageManager packageManager = ApplicationLoader.getAppContext().getPackageManager();
        String packageName = ApplicationLoader.getAppContext().getPackageName();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            version = "ERR";
        }
        return version;
    }

    /**
     * Retrieves the first installation time of the application.
     *
     * @return The first installation time of the application.
     */
    public static long getFirstInstallTime() {
        long version = 0;
        PackageManager packageManager = ApplicationLoader.getAppContext().getPackageManager();
        String packageName = ApplicationLoader.getAppContext().getPackageName();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            version = packageInfo.firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * Retrieves the last update time of the application.
     *
     * @return The last update time of the application.
     */
    public static long getLastUpdateTime() {
        long version = 0;
        PackageManager packageManager = ApplicationLoader.getAppContext().getPackageManager();
        String packageName = ApplicationLoader.getAppContext().getPackageName();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            version = packageInfo.lastUpdateTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * Retrieves the network operator name.
     *
     * @return The network operator name.
     */
    public static String getNetworkOperatorName() {
        if (telephonyManager != null) {
            String operatorName = telephonyManager.getNetworkOperatorName();

            if (operatorName != null && !operatorName.isEmpty()) {
                Log.d("NetworkUtils", "Operator Name: " + operatorName);
                return operatorName;
            } else {
                Log.d("NetworkUtils", "Unable to retrieve operator name");
            }
        } else {
            Log.d("NetworkUtils", "TelephonyManager is null");
        }

        return "";
    }

    /**
     * Retrieves the percentage of free memory on the device.
     *
     * @return The percentage of free memory.
     */
    public static int getFreeMemoryPercentage() {
        ActivityManager activityManager = (ActivityManager) ApplicationLoader.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return (int) Math.round((double) memoryInfo.availMem / memoryInfo.totalMem * 100.0);
    }

    /**
     * Retrieves the SIM country ISO.
     *
     * @return The SIM country ISO.
     */
    public static String getSimCountryIso() {
        return telephonyManager.getSimCountryIso();
    }

    /**
     * Retrieves the SIM operator.
     *
     * @return The SIM operator.
     */
    public static String getSimOperator() {
        return telephonyManager.getSimOperator();
    }

    /**
     * Retrieves the battery percentage.
     *
     * @return The battery percentage.
     */
    public static int getBatteryPercentage() {
        BatteryManager batteryManager = (BatteryManager) ApplicationLoader.getAppContext().getSystemService(Context.BATTERY_SERVICE);
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    /**
     * Retrieves the network class.
     *
     * @return The network class.
     */
    public static String getNetworkClass() {
        ConnectivityManager cm = (ConnectivityManager) ApplicationLoader.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !info.isConnectedOrConnecting())
            return "NOT_CONNECTED"; // not connected
        if (info.getType() == ConnectivityManager.TYPE_WIFI)
            return "WIFI";
        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int networkType = info.getSubtype();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:     // api< 8: replace by 11
                case TelephonyManager.NETWORK_TYPE_GSM:      // api<25: replace by 16
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:   // api< 9: replace by 12
                case TelephonyManager.NETWORK_TYPE_EHRPD:    // api<11: replace by 14
                case TelephonyManager.NETWORK_TYPE_HSPAP:    // api<13: replace by 15
                case TelephonyManager.NETWORK_TYPE_TD_SCDMA: // api<25: replace by 17
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_LTE:      // api<11: replace by 13
                case TelephonyManager.NETWORK_TYPE_IWLAN:    // api<25: replace by 18
                case 19: // LTE_CA
                    return "4G";
                case TelephonyManager.NETWORK_TYPE_NR:       // api<29: replace by 20
                    return "5G";
                default:
                    return "?";
            }
        }
        return "?";
    }

    /**
     * Checks if airplane mode is enabled.
     *
     * @return True if airplane mode is enabled, false otherwise.
     */
    public static boolean isAirplaneModeOn() {
        return Settings.Global.getInt(ApplicationLoader.getAppContext().getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    /**
     * Retrieves the model of the device.
     *
     * @return The device model.
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * Retrieves the manufacturer of the device.
     *
     * @return The device manufacturer.
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * Retrieves the brand of the device.
     *
     * @return The device brand.
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * Retrieves the Android OS version of the device.
     *
     * @return The Android OS version.
     */
    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * Generates a unique identifier for the device based on its fingerprint and Android ID.
     *
     * @return The device unique identifier.
     */
    public static String getDeviceUniqueId() {
        @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(
                ApplicationLoader.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String fingerprint = Build.FINGERPRINT;
        if (androidId == null || "9774d56d682e549c".equals(androidId)) {
            androidId = UUID.randomUUID().toString();
        }
        return fingerprint.concat("/").concat(androidId);
    }

    /**
     * Retrieves the app signatures using SHA-256 hashing algorithm.
     *
     * @return List of app signatures.
     */
    public static ArrayList<String> getAppSignatures() {
        ArrayList<String> appCodes = new ArrayList<>();
        try {
            String packageName = ApplicationLoader.getAppContext().getPackageName();
            PackageManager packageManager = ApplicationLoader.getAppContext().getPackageManager();
            Signature[] signatures = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures;
            for (Signature signature : signatures) {
                String hash = getAppHash(packageName, signature.toCharsString());
                if (hash != null) {
                    appCodes.add(String.format("%s", hash));
                }
            }
        } catch (PackageManager.NameNotFoundException ignore) {
            // Ignore exception if package name is not found.
        }
        return appCodes;
    }

    /**
     * Generates a hash for the app using SHA-256 algorithm.
     *
     * @param packageName The package name of the app.
     * @param signature   The app signature.
     * @return The generated app hash.
     */
    @Nullable
    private static String getAppHash(String packageName, String signature) {
        String appInfo = packageName + " " + signature;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASH_TYPE_SHA_256);
            messageDigest.update(appInfo.getBytes(StandardCharsets.UTF_8));
            byte[] hashSignature = messageDigest.digest();
            hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES);
            String base64Hash = Base64.encodeToString(hashSignature, Base64.NO_PADDING | Base64.NO_WRAP);
            base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR);
            return base64Hash;
        } catch (NoSuchAlgorithmException ignore) {
            // Ignore exception if SHA-256 algorithm is not available.
        }
        return null;
    }
}