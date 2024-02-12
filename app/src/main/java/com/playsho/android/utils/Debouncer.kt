package com.playsho.android.utils

import android.os.Handler
import android.os.Looper

/**
 * A utility class for debouncing operations in Android applications.
 *
 * Debouncing is a technique used to limit the frequency of execution of a particular action or task,
 * ensuring that it only happens after a specified delay without any additional triggers.
 *
 * This class allows you to debounce a Runnable, preventing it from running until a certain amount
 * of time has passed without further triggers. It's particularly useful in scenarios where you want
 * to delay or prevent rapid, repetitive actions such as button clicks or user input.
 */
class Debouncer(private val delayMillis: Long) {
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    /**
     * Debounces a Runnable, ensuring it is executed after the specified delay, and canceling
     * any previously scheduled executions.
     *
     * @param runnable The Runnable to be debounced.
     */
    fun debounce(runnable: Runnable) {
        // Cancel the previous runnable if it exists
        cancel()

        this.runnable = runnable
        handler.postDelayed(runnable, delayMillis)
    }

    /**
     * Cancels any pending execution of the debounced Runnable.
     */
    fun cancel() {
        runnable?.let {
            handler.removeCallbacks(it)
            runnable = null
        }
    }
}