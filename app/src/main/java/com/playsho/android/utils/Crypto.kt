package com.playsho.android.utils

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Crypto {

    private const val AES_ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val AES_IV_SIZE = 16 // 16 bytes for AES

    private fun generateIV(): ByteArray {
        val iv = ByteArray(AES_IV_SIZE) // 16 bytes for AES
        SecureRandom().nextBytes(iv)
        return iv
    }

    private fun hexStringToByteArray(hexString: String): ByteArray {
        val len = hexString.length
        require(len % 2 == 0) { "Hex string length must be even" }
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(hexString[i], 16) shl 4) + Character.digit(
                hexString[i + 1],
                16
            )).toByte()
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
        return Base64.encodeToString(iv, Base64.DEFAULT).plus(":").plus(
            Base64.encodeToString(
                cipherText,
                Base64.DEFAULT
            )
        )
    }

    fun decryptAES(cipherText: String, key: String): String {
        val message = cipherText.split(":")
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val secretKeySpec = SecretKeySpec(hexStringToByteArray(key), AES_ALGORITHM)
        val ivParameterSpec = IvParameterSpec(Base64.decode(message[0], Base64.DEFAULT))
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
        val plainText = cipher.doFinal(Base64.decode(message[1], Base64.DEFAULT))
        return String(plainText)
    }
}