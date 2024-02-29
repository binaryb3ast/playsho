package com.playsho.android.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

object RSAHelper {
    private const val KEY_ALIAS = "my_rsa_key"
    private const val CRYPTO_METHOD = "RSA"
    private const val KEY_PROVIDER = "AndroidKeyStore"

    fun generateKeyPair(): KeyPair {
        // Check if the RSA key pair already exists
        val keyStore = KeyStore.getInstance(KEY_PROVIDER)
        keyStore.load(null)
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            // Generate a new RSA key pair
            val keyPairGenerator = KeyPairGenerator.getInstance(CRYPTO_METHOD, KEY_PROVIDER)
            val spec = KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                KeyProperties.PURPOSE_SIGN
                        or KeyProperties.PURPOSE_VERIFY
                        or KeyProperties.PURPOSE_ENCRYPT
                        or KeyProperties.PURPOSE_DECRYPT
                )
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setKeySize(2048)
                .build()
            keyPairGenerator.initialize(spec)
            return keyPairGenerator.generateKeyPair()
        } else {
            // Load the existing RSA key pair
            val privateKey = keyStore.getKey(KEY_ALIAS, null) as PrivateKey
            val publicKey = keyStore.getCertificate(KEY_ALIAS).publicKey
            return KeyPair(publicKey, privateKey)
        }
    }

    /*** Encrypt with Public Key ***/
    fun encrypt(
        textToEncrypt: String,
        publicKey: PublicKey
    ): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedBytes = cipher.doFinal(textToEncrypt.toByteArray(StandardCharsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    /*** Encrypt with String Public Key ***/
    fun encrypt(
        textToEncrypt: String,
        publicKeyString: String
    ): String {
        val publicKey = stringToPublicKey(publicKeyString)
        return encrypt(
            textToEncrypt = textToEncrypt,
            publicKey = publicKey
        )
    }

    fun decrypt(
        encryptedText: String,
        privateKey: PrivateKey
    ): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedBytes = cipher.doFinal(Base64.decode(encryptedText, Base64.DEFAULT))
        return String(decryptedBytes)
    }


    /*** Prints Public Key String ***/
    fun printPublicKey(keyPairMap: KeyPair): String {
        return String(Base64.encode(keyPairMap.public.encoded, Base64.DEFAULT))
    }

    /*** Converts String Public Key to PublicKey Object ***/
    @Throws(InvalidKeySpecException::class, NoSuchAlgorithmException::class)
    private fun stringToPublicKey(publicKeyString: String): PublicKey {
        val keyBytes: ByteArray = Base64.decode(publicKeyString, Base64.DEFAULT)
        val spec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(CRYPTO_METHOD)
        return keyFactory.generatePublic(spec)
    }

    /*** Converts String Private Key to PrivateKey Object ***/
    @Throws(InvalidKeySpecException::class, NoSuchAlgorithmException::class)
    private fun stringToPrivateKey(privateKeyString: String): PrivateKey {
        val pkcs8EncodedBytes: ByteArray = Base64.decode(privateKeyString, Base64.DEFAULT)
        val keySpec = PKCS8EncodedKeySpec(pkcs8EncodedBytes)
        val kf = KeyFactory.getInstance(CRYPTO_METHOD)
        return kf.generatePrivate(keySpec)
    }
}