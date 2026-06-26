package com.dv.apna.feature.settings.domain.repository

import com.dv.apna.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.dv.apna.feature.settings.domain.model.SettingsModel

interface SettingsRepository {
    fun getData(): Flow<Resource<List<SettingsModel>>>
}