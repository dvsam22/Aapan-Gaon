package com.dv.apna.feature.transport.domain.repository

import com.dv.apna.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.dv.apna.feature.transport.domain.model.TransportModel

interface TransportRepository {
    fun getData(): Flow<Resource<List<TransportModel>>>
}