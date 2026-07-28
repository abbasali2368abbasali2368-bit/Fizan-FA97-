package com.example.util

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object EncryptionUtils {
    private const val ALGORITHM = "AES"
    // 16-byte fixed seed key for local E2E simulation
    private val KEY_BYTES = "CodeChatSecret12".toByteArray(Charsets.UTF_8)

    fun encrypt(plainText: String): String {
        return try {
            val secretKey = SecretKeySpec(KEY_BYTES, ALGORITHM)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            // Fallback encoding
            Base64.encodeToString(plainText.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        }
    }

    fun decrypt(cipherText: String): String {
        return try {
            val secretKey = SecretKeySpec(KEY_BYTES, ALGORITHM)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            val decodedBytes = Base64.decode(cipherText, Base64.NO_WRAP)
            String(cipher.doFinal(decodedBytes), Charsets.UTF_8)
        } catch (e: Exception) {
            try {
                String(Base64.decode(cipherText, Base64.NO_WRAP), Charsets.UTF_8)
            } catch (ex: Exception) {
                cipherText
            }
        }
    }
}
