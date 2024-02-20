package com.playsho.android.utils.accountmanager

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.annotation.Nullable
import com.playsho.android.base.ApplicationLoader

/**
 * Service responsible for managing the Account Authenticator.
 *
 * This service extends the Android Service class and acts as a bridge between the Android
 * AccountManager and the custom AccountAuthenticator. It provides the IBinder to the
 * AccountAuthenticator, allowing the AccountManager to interact with it.
 */
class AccountAuthenticatorService : Service() {

    /**
     * The instance of the custom AccountAuthenticator.
     */
    private val authenticator: AccountAuthenticator = AccountAuthenticator(ApplicationLoader.context)

    /**
     * Called when the service is created. Perform one-time initialization here.
     */
    override fun onCreate() {
        super.onCreate()
        // Perform any additional initialization if needed
    }

    /**
     * Called when the service is bound by the AccountManager.
     *
     * @param intent The intent that was used to bind to this service.
     * @return An IBinder interface to the service.
     */
    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        /*
         * Return the IBinder interface to the AccountAuthenticator.
         * This allows the AccountManager to interact with the AccountAuthenticator.
         */
        return authenticator.iBinder
    }
}