package com.playsho.android.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

object RSAHelper {
    private const val CRYPTO_METHOD = "RSA"

    fun generateKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(CRYPTO_METHOD, KeyStoreHelper.KEY_PROVIDER)
        val spec = KeyGenParameterSpec.Builder(
            KeyStoreHelper.KeyAllies.RSA_KEYS,
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
    }

    fun getKeyPairs(): KeyPair {

        return if (!KeyStoreHelper.containsAlias(KeyStoreHelper.KeyAllies.RSA_KEYS)) {
            // Generate a new RSA key pair
            generateKeyPair()
        } else {
            // Load the existing RSA key pair
            val privateKey:PrivateKey = KeyStoreHelper.getKey(KeyStoreHelper.KeyAllies.RSA_KEYS)
            val publicKey:PublicKey = KeyStoreHelper.getKey(KeyStoreHelper.KeyAllies.RSA_KEYS)
            KeyPair(publicKey, privateKey)
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
        val pemFormat = StringBuilder()
        pemFormat.append("-----BEGIN PUBLIC KEY-----\n")
        pemFormat.append(String(Base64.encode(keyPairMap.public.encoded, Base64.DEFAULT)))
        pemFormat.append("-----END PUBLIC KEY-----")
        return pemFormat.toString()
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