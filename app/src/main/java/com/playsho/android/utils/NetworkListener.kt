package com.playsho.android.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.annotation.NonNull
import com.playsho.android.base.ApplicationLoader
import java.util.concurrent.atomic.AtomicBoolean

/**
 * NetworkListener is a utility class that monitors network availability changes using
 * ConnectivityManager.NetworkCallback. It provides methods to initialize the listener,
 * check the current network availability, and get real-time updates on network status.
 */
class NetworkListener : ConnectivityManager.NetworkCallback() {

    // ConnectivityManager instance for network monitoring
    private val connectivityManager: ConnectivityManager = ApplicationLoader.context
        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // AtomicBoolean to ensure thread-safe access to network availability status
    val isNetworkAvailable = AtomicBoolean(false)

    /**
     * Initializes the NetworkListener by registering it with the ConnectivityManager.
     * This method should be called to start monitoring network changes.
     */
    fun init() {
        // Register the NetworkListener to receive network status callbacks
        connectivityManager.registerDefaultNetworkCallback(this)
    }

    /**
     * Checks the current network availability status.
     *
     * @return True if the network is available, false otherwise.
     */
    fun checkNetworkAvailability(): Boolean {
        // Obtain the currently active network
        val network: Network? = connectivityManager.activeNetwork

        if (network == null) {
            // No active network, set availability to false
            isNetworkAvailable.set(false)
            return isNetworkAvailable()
        }

        // Obtain the network capabilities of the active network
        val networkCapabilities: NetworkCapabilities? = connectivityManager.getNetworkCapabilities(network)

        if (networkCapabilities == null) {
            // Network capabilities not available, set availability to false
            isNetworkAvailable.set(false)
            return isNetworkAvailable()
        }

        // Check if the network has any of the specified transport types (e.g., WiFi, Cellular)
        isNetworkAvailable.set(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH))

        return isNetworkAvailable()
    }

    /**
     * Gets the real-time network availability status.
     *
     * @return True if the network is available, false otherwise.
     */
    fun isNetworkAvailable(): Boolean {
        return isNetworkAvailable.get()
    }

    /**
     * Called when a network becomes available.
     *
     * @param network The Network object representing the available network.
     */
    override fun onAvailable(network: Network) {
        // Set network availability status to true
        isNetworkAvailable.set(true)
    }

    /**
     * Called when a network is lost or becomes unavailable.
     *
     * @param network The Network object representing the lost network.
     */
    override fun onLost(network: Network) {
        // Set network availability status to false
        isNetworkAvailable.set(false)
    }
}
