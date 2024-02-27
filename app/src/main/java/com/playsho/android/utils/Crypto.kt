package com.playsho.android.utils

import android.util.Base64
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Crypto {

    private const val KEY_SIZE: Int = 2048
    private const val RSA_KEY_ALGORITHM: String = "RSA"
    private const val AES_ALGORITHM = "AES"

    private const val RSA_CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val AES_IV_SIZE = 16 // 16 bytes for AES
    private const val AES_KEY_SIZE = 32 // 32 bytes for AES-256
    fun generateRSAKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance(RSA_KEY_ALGORITHM)
        keyGen.initialize(KEY_SIZE) // You can adjust key size as needed
        return keyGen.generateKeyPair()
    }

    fun publicKeyToString(publicKey: PublicKey): String {
        val publicKeyBytes = publicKey.encoded
        return convertToPEM(publicKey)
    }

    fun privateKeyToString(privateKey: PrivateKey): String {
        val privateKeyBytes = privateKey.encoded
        return Base64.encodeToString(privateKeyBytes, Base64.DEFAULT)
    }

    private fun convertToPEM(publicKey: PublicKey): String {
        val encodedKey = publicKey.encoded
        val base64EncodedKey = Base64.encodeToString(encodedKey, Base64.DEFAULT)
        val builder = StringBuilder()
        builder.append("-----BEGIN PUBLIC KEY-----\n")
        builder.append(base64EncodedKey)
        builder.append("-----END PUBLIC KEY-----")
        return builder.toString()
    }

    fun stringToPublicKey(publicKeyStr: String): PublicKey {
        val publicKeyBytes = Base64.decode(publicKeyStr, Base64.DEFAULT)
        val keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM)
        val publicKeySpec = X509EncodedKeySpec(publicKeyBytes)
        return keyFactory.generatePublic(publicKeySpec)
    }

    fun stringToPrivateKey(privateKeyStr: String): PrivateKey {
        val privateKeyBytes = Base64.decode(privateKeyStr, Base64.NO_PADDING or Base64.NO_WRAP)
        val keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM)
        val privateKeySpec = PKCS8EncodedKeySpec(privateKeyBytes)
        return keyFactory.generatePrivate(privateKeySpec)
    }

    fun encryptMessage(message: String, publicKey: PublicKey): String {
        val cipher = Cipher.getInstance(RSA_CIPHER_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedBytes = cipher.doFinal(message.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    fun decryptMessage(encryptedMessage: String, privateKey: PrivateKey): String {
        val encryptedBytes = Base64.decode(encryptedMessage, Base64.DEFAULT)
        val cipher = Cipher.getInstance(RSA_CIPHER_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }

    fun generateIV(): ByteArray {
        val iv = ByteArray(16) // 16 bytes for AES
        SecureRandom().nextBytes(iv)
        return iv;
    }

    fun hexStringToByteArray(hexString: String): ByteArray {
        val len = hexString.length
        require(len % 2 == 0) { "Hex string length must be even" }
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(hexString[i], 16) shl 4) + Character.digit(hexString[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    fun encryptAES(plainText: String, key: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val secretKeySpec = SecretKeySpec(hexStringToByteArray(key), AES_ALGORITHM)
        val iv: ByteArray = generateIV()
        val ivParameterSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
        val cipherText = cipher.doFinal(plainText.toByteArray())
        return Base64.encodeToString(iv, Base64.DEFAULT).plus(":").plus(Base64.encodeToString(
            cipherText,
            Base64.DEFAULT
        ))
    }

    fun decryptAES(cipherText: String, key: String): String {
        val message = cipherText.split(":");
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val secretKeySpec = SecretKeySpec(hexStringToByteArray(key), AES_ALGORITHM)
        val ivParameterSpec = IvParameterSpec(Base64.decode(message[0],Base64.DEFAULT))
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
        val plainText = cipher.doFinal(Base64.decode(message[1], Base64.DEFAULT))
        return String(plainText)
    }
}