package com.fakehealth.data

import android.app.Application
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.rxjava3.map
import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SecretPrefs(private val dataStore: androidx.datastore.preferences.PreferencesDataStore) {

    private val KEY_HAS_PIN = booleanPreferencesKey("has_pin")
    private val KEY_PIN_HASH = stringPreferencesKey("pin_hash")
    private val KEY_SECRET_MODE = booleanPreferencesKey("secret_mode_enabled")
    private val KEY_TAP_COUNT = intPreferencesKey("tap_count")
    private val KEY_LAST_TAP_TIME = stringPreferencesKey("last_tap_time")

    val hasPin: Flow<Boolean> = dataStore.data.map { it[KEY_HAS_PIN] ?: false }
    val secretModeEnabled: Flow<Boolean> = dataStore.data.map { it[KEY_SECRET_MODE] ?: false }

    suspend fun setPin(pin: String) {
        val hash = hashPin(pin)
        dataStore.edit { prefs ->
            prefs[KEY_HAS_PIN] = true
            prefs[KEY_PIN_HASH] = hash
        }
    }

    suspend fun verifyPin(pin: String): Boolean {
        val storedHash = dataStore.data.first() [KEY_PIN_HASH] ?: return false
        return hashPin(pin) == storedHash
    }

    suspend fun clearPin() {
        dataStore.edit { prefs ->
            prefs[KEY_HAS_PIN] = false
            prefs[KEY_PIN_HASH] = ""
        }
    }

    suspend fun setSecretMode(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_SECRET_MODE] = enabled
        }
    }

    suspend fun registerTap(): Boolean {
        val now = System.currentTimeMillis()
        val prefs = dataStore.data.first()
        val lastTapTime = prefs[KEY_LAST_TAP_TIME]?.toLong() ?: 0
        val tapCount = if (now - lastTapTime < 2000) prefs[KEY_TAP_COUNT] ?: 0 else 0

        val newCount = tapCount + 1
        dataStore.edit { prefs ->
            prefs[KEY_TAP_COUNT] = newCount
            prefs[KEY_LAST_TAP_TIME] = now.toString()
        }
        return newCount >= 5 // 5 быстрых тапов = секретный жест
    }

    suspend fun resetTapCount() {
        dataStore.edit { prefs ->
            prefs[KEY_TAP_COUNT] = 0
        }
    }

    private fun hashPin(pin: String): String {
        // Простой hash для демо (в продакшене использовать BCrypt/Argon2)
        return pin.hashCode().toString()
    }

    companion object {
        @Volatile private var INSTANCE: SecretPrefs? = null

        fun getInstance(application: Application): SecretPrefs {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SecretPrefs(
                    (application as FakeHealthApplication).dataStore
                ).also { INSTANCE = it }
            }
        }
    }
}