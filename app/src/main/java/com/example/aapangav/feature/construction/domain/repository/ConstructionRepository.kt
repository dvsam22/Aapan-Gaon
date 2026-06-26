package com.example.aapangav.feature.construction.domain.repository

import com.example.aapangav.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.example.aapangav.feature.construction.domain.model.ConstructionModel

interface ConstructionRepository {
    fun getData(): Flow<Resource<List<ConstructionModel>>>
}