package com.example.aapangav.feature.health.domain.repository

import com.example.aapangav.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.example.aapangav.feature.health.domain.model.HealthModel

interface HealthRepository {
    fun getData(): Flow<Resource<List<HealthModel>>>
}