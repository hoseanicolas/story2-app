package com.sample.storyapp2.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val tokenKey = stringPreferencesKey("token")
    private val userIdKey = stringPreferencesKey("user_id")
    private val nameKey = stringPreferencesKey("name")

    fun getToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[tokenKey]
        }
    }

    suspend fun saveSession(token: String, userId: String, name: String) {
        dataStore.edit { preferences ->
            preferences[tokenKey] = token
            preferences[userIdKey] = userId
            preferences[nameKey] = name
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(context: Context): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(context.dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}