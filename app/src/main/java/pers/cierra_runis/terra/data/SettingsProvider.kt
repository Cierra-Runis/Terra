package pers.cierra_runis.terra.data

import android.util.*
import androidx.datastore.core.*
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.*
import java.io.*


class SettingsProvider(private val dataStore: DataStore<Preferences>) {
    private companion object {
        val QWeather_API_KEY = stringPreferencesKey("qweather_api_key")
        val QWeather_PUBLIC_ID = stringPreferencesKey("qweather_public_id")
        const val TAG = "SettingsProvider"
    }


    val qWeatherApiKey: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading ${QWeather_API_KEY}.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[QWeather_API_KEY] ?: ""
        }

    suspend fun saveQWeatherApiKey(value: String) {
        dataStore.edit { preferences -> preferences[QWeather_API_KEY] = value }
    }


    val qWeatherPublicKey: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading ${QWeather_PUBLIC_ID}.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[QWeather_PUBLIC_ID] ?: ""
        }

    suspend fun saveQWeatherPublicId(value: String) {
        dataStore.edit { preferences -> preferences[QWeather_PUBLIC_ID] = value }
    }
}

