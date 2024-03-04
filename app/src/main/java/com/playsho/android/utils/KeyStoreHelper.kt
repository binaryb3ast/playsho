package com.playsho.android.utils

import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.cert.Certificate

object KeyStoreHelper {
    const val KEY_PROVIDER = "AndroidKeyStore"
    private val keyStoreInstance: KeyStore by lazy {
        KeyStore.getInstance(KEY_PROVIDER).apply {
            loadKeyStore()
        }
    }

    object KeyAllies {
        const val RSA_KEYS = "rsa_key"
    }

    fun getInstance(): KeyStore {
        return keyStoreInstance
    }

    private fun KeyStore.loadKeyStore() {
        runCatching {
            load(null)
        }.onFailure {
            // Handle error appropriately
            it.printStackTrace()
        }
    }

    fun containsAlias(keyAlias: String): Boolean {
        return try {
            getInstance().containsAlias(keyAlias)
        } catch (e: Exception) {
            // Handle error appropriately
            e.printStackTrace()
            false
        }
    }

    inline fun <reified T> getKey(keyAlias: String, pass: CharArray? = null): T {
        return try {
            when (T::class) {
                PrivateKey::class -> getInstance().getKey(keyAlias, pass) as T
                PublicKey::class -> getInstance().getCertificate(keyAlias)?.publicKey as T
                Certificate::class -> getInstance().getCertificate(keyAlias) as T
                else -> throw IllegalArgumentException("Unsupported key type")
            }
        } catch (e: Exception) {
            // Handle error appropriately
            e.printStackTrace()
            throw IllegalStateException("Error retrieving key")
        }
    }

}