package com.example.aapangav.feature.mandi.domain.repository

import com.example.aapangav.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.example.aapangav.feature.mandi.domain.model.MandiModel

interface MandiRepository {
    fun getData(): Flow<Resource<List<MandiModel>>>
}