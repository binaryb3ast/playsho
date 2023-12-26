package com.playsho.android.utils;

import android.os.Handler;
import android.os.Looper;

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
public class Debouncer {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private final long delayMillis;

    /**
     * Constructs a Debouncer with the specified delay interval.
     *
     * @param delayMillis The delay interval in milliseconds. The Runnable will be executed after
     *                    this delay, as long as no new debounce requests are made.
     */
    public Debouncer(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    /**
     * Debounces a Runnable, ensuring it is executed after the specified delay, and canceling
     * any previously scheduled executions.
     *
     * @param runnable The Runnable to be debounced.
     */
    public void debounce(Runnable runnable) {
        // Cancel the previous runnable if it exists
        cancel();

        this.runnable = runnable;
        handler.postDelayed(runnable, delayMillis);
    }

    /**
     * Cancels any pending execution of the debounced Runnable.
     */
    public void cancel() {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
            runnable = null;
        }
    }
}
