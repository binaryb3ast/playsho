package com.playsho.android.utils

import android.util.Patterns

/**
 * A utility class for performing various validation checks on strings and values.
 */
object Validator {

    /**
     * Checks if a string is null or empty.
     *
     * @param string The string to check.
     * @return `true` if the string is null or empty, `false` otherwise.
     */
    fun isNullOrEmpty(string: String?): Boolean {
        return string.isNullOrEmpty()
    }


    fun isUrl(text: String): Boolean {
        val pattern = Patterns.WEB_URL
        val matcher = pattern.matcher(text)
        return matcher.matches()
    }

    /**
     * Checks if a reference is null.
     *
     * @param reference The reference to check.
     * @return `true` if the reference is null, `false` otherwise.
     */
    fun <T> isNull(reference: T?): Boolean {
        return reference == null
    }

    /**
     * Checks if a string is a valid Iran phone number.
     *
     * @param phone The phone number to check.
     * @return `true` if the phone number is a valid Iran phone number, `false` otherwise.
     */
    fun isIranPhoneNumber(phone: String?): Boolean {
        return !phone.isNullOrEmpty() && (phone.startsWith("09") || phone.startsWith("+98")) && phone.length >= 11
    }

    /**
     * Checks if a string has a specific length.
     *
     * @param content The string to check.
     * @param length The expected length.
     * @return `true` if the string has the specified length, `false` otherwise.
     */
    fun hasLength(content: String?, length: Int): Boolean {
        return !content.isNullOrEmpty() && content.length == length
    }

    /**
     * Checks if the length of a string is greater than a specified value.
     *
     * @param content The string to check.
     * @param length The minimum length.
     * @return `true` if the string length is greater than the specified value, `false` otherwise.
     */
    fun isGreaterThan(content: String?, length: Int): Boolean {
        return !content.isNullOrEmpty() && content.length > length
    }

    /**
     * Gets the string value or a default if it is null or empty.
     *
     * @param s The string to check.
     * @param def The default value.
     * @return The original string if not null or empty, otherwise the default value.
     */
    fun getDefaultIfNullOrEmpty(s: String?, def: String): String {
        return s ?: def
    }

    /**
     * Checks if the length of a string is smaller than a specified value.
     *
     * @param content The string to check.
     * @param length The maximum length.
     * @return `true` if the string length is smaller than the specified value, `false` otherwise.
     */
    fun isSmallerThan(content: String?, length: Int): Boolean {
        return !content.isNullOrEmpty() && content.length < length
    }

    /**
     * Checks if two integers are equal.
     *
     * @param a The first integer.
     * @param b The second integer.
     * @return `true` if the integers are equal, `false` otherwise.
     */
    fun isEqual(a: Int, b: Int): Boolean {
        return a == b
    }

    /**
     * Checks if two strings are equal (handling null values).
     *
     * @param a The first string.
     * @param b The second string.
     * @return `true` if the strings are equal, `false` otherwise.
     */
    fun isEqual(a: String?, b: String?): Boolean {
        return a == b
    }
}
