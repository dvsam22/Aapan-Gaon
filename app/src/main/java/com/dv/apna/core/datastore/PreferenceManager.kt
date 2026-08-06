package com.dv.apna.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val VILLAGE_ID = stringPreferencesKey("village_id")
        private val VILLAGE_NAME = stringPreferencesKey("village_name")
        private val VILLAGE_LAT = doublePreferencesKey("village_lat")
        private val VILLAGE_LNG = doublePreferencesKey("village_lng")
        private val LANGUAGE_CODE = stringPreferencesKey("language_code")
    }

    suspend fun saveVillage(id: String, name: String, lat: Double = 0.0, lng: Double = 0.0) {
        context.dataStore.edit { preferences ->
            preferences[VILLAGE_ID] = id
            preferences[VILLAGE_NAME] = name
            preferences[VILLAGE_LAT] = lat
            preferences[VILLAGE_LNG] = lng
        }
    }

    val villageId: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[VILLAGE_ID]
    }

    val villageName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[VILLAGE_NAME]
    }

    val villageLat: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[VILLAGE_LAT] ?: 0.0
    }

    val villageLng: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[VILLAGE_LNG] ?: 0.0
    }

    suspend fun saveLanguage(code: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_CODE] = code
        }
    }

    val languageCode: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_CODE]
    }
}
