package com.dv.apna.core.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "settings")

class PreferenceManager @Inject constructor(private val context: Context) {
    // DataStore implementation
}