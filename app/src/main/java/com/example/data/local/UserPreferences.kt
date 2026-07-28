package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.util.CodeGenerator

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("codechat_prefs", Context.MODE_PRIVATE)

    fun getOrGenerateUserCode(): String {
        var code = prefs.getString(KEY_USER_CODE, null)
        if (code.isNullOrEmpty()) {
            val generated = CodeGenerator.generateUniqueCode()
            prefs.edit().putString(KEY_USER_CODE, generated).apply()
            return generated
        }
        return code
    }

    fun getUserCode(): String {
        return getOrGenerateUserCode()
    }

    fun getUserName(): String {
        val code = getUserCode()
        return prefs.getString(KEY_USER_NAME, null) ?: "User ${code.take(4)}"
    }

    fun setUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name).apply()
    }

    fun getUserBio(): String {
        return prefs.getString(KEY_USER_BIO, "Available - Chatting via CodeChat") ?: "Available"
    }

    fun setUserBio(bio: String) {
        prefs.edit().putString(KEY_USER_BIO, bio).apply()
    }

    fun getUserAvatarColorHex(): String {
        return prefs.getString(KEY_AVATAR_COLOR, "#00A884") ?: "#00A884"
    }

    fun setUserAvatarColorHex(hex: String) {
        prefs.edit().putString(KEY_AVATAR_COLOR, hex).apply()
    }

    fun getThemeMode(): String {
        return prefs.getString(KEY_THEME_MODE, "DARK") ?: "DARK"
    }

    fun setThemeMode(mode: String) {
        prefs.edit().putString(KEY_THEME_MODE, mode).apply()
    }

    fun isNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS, true)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
    }

    fun isReadReceiptsEnabled(): Boolean {
        return prefs.getBoolean(KEY_READ_RECEIPTS, true)
    }

    fun setReadReceiptsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_READ_RECEIPTS, enabled).apply()
    }

    fun isTypingIndicatorsEnabled(): Boolean {
        return prefs.getBoolean(KEY_TYPING_INDICATORS, true)
    }

    fun setTypingIndicatorsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_TYPING_INDICATORS, enabled).apply()
    }

    companion object {
        private const val KEY_USER_CODE = "key_user_code"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_USER_BIO = "key_user_bio"
        private const val KEY_AVATAR_COLOR = "key_avatar_color"
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_NOTIFICATIONS = "key_notifications"
        private const val KEY_READ_RECEIPTS = "key_read_receipts"
        private const val KEY_TYPING_INDICATORS = "key_typing_indicators"
    }
}

private fun String?.isNull_or_empty(): Boolean = this == null || this.isEmpty()
