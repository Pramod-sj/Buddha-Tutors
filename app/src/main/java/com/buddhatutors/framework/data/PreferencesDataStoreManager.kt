package com.buddhatutors.framework.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.buddhatutors.domain.PreferenceManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jetbrains.annotations.ApiStatus.Internal
import javax.inject.Inject

internal class PreferencesDataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferenceManager<String> {

    override suspend fun set(key: String, data: String) {
        val preferenceKey = stringPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[preferenceKey] = data
        }
    }

    override fun get(key: String): Flow<String?> {
        val preferenceKey = stringPreferencesKey(key)
        return dataStore.data.map { preferences ->
            preferences[preferenceKey]
        }
    }

    override suspend fun remove(key: String) {
        val preferenceKey = stringPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences.remove(preferenceKey)
        }
    }
}
