package com.playsho.android.utils;

import android.text.TextUtils;

import java.util.Objects;

/**
 * A utility class for performing various validation checks on strings and values.
 */
public class Validator {

    /**
     * Checks if a string is null or empty.
     *
     * @param string The string to check.
     * @return {@code true} if the string is null or empty, {@code false} otherwise.
     */
    public static boolean isNullOrEmpty(String string) {
        return TextUtils.isEmpty(string);
    }

    /**
     * Checks if a reference is null.
     *
     * @param reference The reference to check.
     * @param <T>       The type of the reference.
     * @return {@code true} if the reference is null, {@code false} otherwise.
     */
    public static <T> boolean isNull(T reference) {
        return reference == null;
    }

    /**
     * Checks if a string is a valid Iran phone number.
     *
     * @param phone The phone number to check.
     * @return {@code true} if the phone number is a valid Iran phone number, {@code false} otherwise.
     */
    public static boolean isIranPhoneNumber(String phone) {
        if (isNullOrEmpty(phone)) return false;
        boolean starter = phone.startsWith("09") || phone.startsWith("+98");
        return starter && phone.length() >= 11;
    }

    /**
     * Checks if a string has a specific length.
     *
     * @param content The string to check.
     * @param length  The expected length.
     * @return {@code true} if the string has the specified length, {@code false} otherwise.
     */
    public static boolean hasLength(String content, int length) {
        return !isNullOrEmpty(content) && content.length() == length;
    }

    /**
     * Checks if the length of a string is greater than a specified value.
     *
     * @param content The string to check.
     * @param length  The minimum length.
     * @return {@code true} if the string length is greater than the specified value, {@code false} otherwise.
     */
    public static boolean isGreaterThan(String content, int length) {
        return !isNullOrEmpty(content) && content.length() > length;
    }

    /**
     * Gets the string value or a default if it is null or empty.
     *
     * @param s   The string to check.
     * @param def The default value.
     * @return The original string if not null or empty, otherwise the default value.
     */
    public static String getDefaultIfNullOrEmpty(String s, String def) {
        return isNullOrEmpty(s) ? def : s;
    }

    /**
     * Checks if the length of a string is smaller than a specified value.
     *
     * @param content The string to check.
     * @param length  The maximum length.
     * @return {@code true} if the string length is smaller than the specified value, {@code false} otherwise.
     */
    public static boolean isSmallerThan(String content, int length) {
        return !isNullOrEmpty(content) && content.length() < length;
    }

    /**
     * Checks if two integers are equal.
     *
     * @param a The first integer.
     * @param b The second integer.
     * @return {@code true} if the integers are equal, {@code false} otherwise.
     */
    public static boolean isEqual(int a, int b) {
        return a == b;
    }

    /**
     * Checks if two strings are equal (handling null values).
     *
     * @param a The first string.
     * @param b The second string.
     * @return {@code true} if the strings are equal, {@code false} otherwise.
     */
    public static boolean isEqual(String a, String b) {
        return Objects.equals(a, b);
    }
}