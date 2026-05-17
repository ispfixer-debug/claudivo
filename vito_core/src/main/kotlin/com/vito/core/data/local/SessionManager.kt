package com.vito.core.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SessionManager - stores JWT in EncryptedSharedPreferences.
 * Per RULE #3 - JWT in EncryptedSharedPreferences — never DataStore for session.
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        private const val PREFS_NAME = "vito_session"
        private const val KEY_JWT = "jwt"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_TYPE = "user_type"  // "client" | "driver" | "admin"
        private const val KEY_USERNAME = "username"
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_EXPIRES_AT = "expires_at"
    }

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val prefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    var jwt: String?
        get() = prefs.getString(KEY_JWT, null)
        set(value) = prefs.edit().putString(KEY_JWT, value).apply()

    var userId: String?
        get() = prefs.getString(KEY_USER_ID, null)
        set(value) = prefs.edit().putString(KEY_USER_ID, value).apply()

    var userType: String?
        get() = prefs.getString(KEY_USER_TYPE, null)
        set(value) = prefs.edit().putString(KEY_USER_TYPE, value).apply()

    var username: String?
        get() = prefs.getString(KEY_USERNAME, null)
        set(value) = prefs.edit().putString(KEY_USERNAME, value).apply()

    var pinHash: String?
        get() = prefs.getString(KEY_PIN_HASH, null)
        set(value) = prefs.edit().putString(KEY_PIN_HASH, value).apply()

    var expiresAt: Long
        get() = prefs.getLong(KEY_EXPIRES_AT, 0L)
        set(value) = prefs.edit().putLong(KEY_EXPIRES_AT, value).apply()

    val isLoggedIn: Boolean
        get() = !jwt.isNullOrBlank() && (expiresAt == 0L || expiresAt > System.currentTimeMillis())

    fun saveSession(jwt: String, userId: String, userType: String, username: String, pinHash: String, expiresAt: Long) {
        prefs.edit()
            .putString(KEY_JWT, jwt)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_TYPE, userType)
            .putString(KEY_USERNAME, username)
            .putString(KEY_PIN_HASH, pinHash)
            .putLong(KEY_EXPIRES_AT, expiresAt)
            .apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
