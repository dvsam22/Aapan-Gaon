package com.example.aapangav.feature.settings.data.repository

import com.example.aapangav.core.common.Resource
import com.example.aapangav.feature.settings.domain.model.SettingsModel
import com.example.aapangav.feature.settings.domain.repository.SettingsRepository
import com.example.aapangav.feature.settings.data.datasource.SettingsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataSource: SettingsDataSource
) : SettingsRepository {
    override fun getData(): Flow<Resource<List<SettingsModel>>> = flow {
        emit(Resource.Loading())
    }
}