package com.example.aapangav.feature.settings.domain.repository

import com.example.aapangav.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.example.aapangav.feature.settings.domain.model.SettingsModel

interface SettingsRepository {
    fun getData(): Flow<Resource<List<SettingsModel>>>
}