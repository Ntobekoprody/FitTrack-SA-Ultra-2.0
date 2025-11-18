package com.fittracksa.app.services

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

/**
 * Simple secure credential store using EncryptedSharedPreferences.
 * - saveCredentials(context, email, password)
 * - getCredentials(context) -> Pair(email, password)? or null
 * - clearCredentials(context)
 *
 * NOTE: store credentials only when user explicitly opts in to biometric login.
 */
object CredentialStore {
    private const val PREF_FILE = "fittrack_secure_prefs"
    private const val KEY_EMAIL = "saved_email"
    private const val KEY_PASSWORD = "saved_password"
    private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"

    private fun getPrefs(context: Context) =
        EncryptedSharedPreferences.create(
            PREF_FILE,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    fun saveCredentials(context: Context, email: String, password: String) {
        try {
            val prefs = getPrefs(context)
            prefs.edit().putString(KEY_EMAIL, email).putString(KEY_PASSWORD, password).apply()
            prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, true).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCredentials(context: Context): Pair<String, String>? {
        return try {
            val prefs = getPrefs(context)
            val email = prefs.getString(KEY_EMAIL, null)
            val pass = prefs.getString(KEY_PASSWORD, null)
            if (email != null && pass != null) Pair(email, pass) else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun clearCredentials(context: Context) {
        try {
            val prefs = getPrefs(context)
            prefs.edit().clear().apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isBiometricEnabled(context: Context): Boolean {
        return try {
            getPrefs(context).getBoolean(KEY_BIOMETRIC_ENABLED, false)
        } catch (e: Exception) {
            false
        }
    }

    fun setBiometricEnabled(context: Context, enabled: Boolean) {
        try {
            getPrefs(context).edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
            if (!enabled) clearCredentials(context)
        } catch (e: Exception) { }
    }
}
