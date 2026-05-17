package com.vito.core.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "vito_secure_session",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    companion object {
        private const val KEY_JWT = "jwt"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_TYPE = "user_type"
        private const val KEY_USERNAME = "username"
        private const val KEY_DISPLAY_NAME = "display_name"
    }

    fun saveSession(
        jwt: String,
        userId: String,
        userType: String,
        username: String,
        displayName: String,
    ) = prefs.edit()
        .putString(KEY_JWT, jwt)
        .putString(KEY_USER_ID, userId)
        .putString(KEY_USER_TYPE, userType)
        .putString(KEY_USERNAME, username)
        .putString(KEY_DISPLAY_NAME, displayName)
        .apply()

    fun getJwt(): String? = prefs.getString(KEY_JWT, null)
    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
    fun getUserType(): String? = prefs.getString(KEY_USER_TYPE, null)
    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)
    fun getDisplayName(): String? = prefs.getString(KEY_DISPLAY_NAME, null)
    fun isLoggedIn(): Boolean = getJwt() != null
    fun clearSession() = prefs.edit().clear().apply()
}
