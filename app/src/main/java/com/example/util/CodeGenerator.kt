package com.example.util

import java.security.SecureRandom

object CodeGenerator {
    private const val CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    private const val CODE_LENGTH = 8
    private val random = SecureRandom()

    fun generateUniqueCode(): String {
        val sb = StringBuilder(CODE_LENGTH)
        for (i in 0 until CODE_LENGTH) {
            val randomIndex = random.nextInt(CHAR_POOL.length)
            sb.append(CHAR_POOL[randomIndex])
        }
        return sb.toString()
    }

    fun isValidCode(code: String): Boolean {
        val trimmed = code.trim().uppercase()
        if (trimmed.length != CODE_LENGTH) return false
        return trimmed.all { it in CHAR_POOL }
    }

    fun formatCode(code: String): String {
        return code.trim().uppercase()
    }
}
