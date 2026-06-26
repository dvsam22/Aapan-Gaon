package com.dv.apna.feature.construction.domain.repository

import com.dv.apna.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.dv.apna.feature.construction.domain.model.ConstructionModel

interface ConstructionRepository {
    fun getData(): Flow<Resource<List<ConstructionModel>>>
}