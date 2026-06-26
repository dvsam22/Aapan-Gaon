package com.dv.apna.feature.health.domain.repository

import com.dv.apna.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.dv.apna.feature.health.domain.model.HealthModel

interface HealthRepository {
    fun getData(): Flow<Resource<List<HealthModel>>>
}