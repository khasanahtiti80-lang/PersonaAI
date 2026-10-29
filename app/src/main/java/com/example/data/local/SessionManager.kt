package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("persona_ai_session", Context.MODE_PRIVATE)

    private val _currentUserId = MutableStateFlow<Long?>(
        if (prefs.contains(KEY_USER_ID)) prefs.getLong(KEY_USER_ID, -1L).takeIf { it != -1L } else null
    )
    val currentUserId: StateFlow<Long?> = _currentUserId.asStateFlow()

    private val _customApiKey = MutableStateFlow<String>(
        prefs.getString(KEY_CUSTOM_API_KEY, "") ?: ""
    )
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    fun loginUser(userId: Long) {
        prefs.edit().putLong(KEY_USER_ID, userId).apply()
        _currentUserId.value = userId
    }

    fun logout() {
        prefs.edit().remove(KEY_USER_ID).apply()
        _currentUserId.value = null
    }

    fun saveCustomApiKey(key: String) {
        val trimmed = key.trim()
        prefs.edit().putString(KEY_CUSTOM_API_KEY, trimmed).apply()
        _customApiKey.value = trimmed
    }

    fun getEffectiveApiKey(): String {
        val custom = _customApiKey.value.trim()
        if (custom.isNotBlank()) return custom
        return try {
            val buildConfigKey = BuildConfig.GEMINI_API_KEY
            if (!buildConfigKey.isNullOrBlank() && buildConfigKey != "MY_GEMINI_API_KEY") {
                buildConfigKey
            } else ""
        } catch (_: Throwable) {
            ""
        }
    }

    fun hasApiKey(): Boolean {
        return getEffectiveApiKey().isNotBlank()
    }

    companion object {
        private const val KEY_USER_ID = "current_user_id"
        private const val KEY_CUSTOM_API_KEY = "custom_gemini_api_key"

        fun hashPassword(password: String): String {
            val salt = "persona_ai_secure_salt_2026"
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest((password + salt).toByteArray(Charsets.UTF_8))
            return digest.fold("") { str, it -> str + "%02x".format(it) }
        }
    }
}
