package com.example.aapangav.feature.transport.domain.repository

import com.example.aapangav.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.example.aapangav.feature.transport.domain.model.TransportModel

interface TransportRepository {
    fun getData(): Flow<Resource<List<TransportModel>>>
}