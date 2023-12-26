package com.playsho.android.utils;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;

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