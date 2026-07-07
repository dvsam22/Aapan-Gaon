package com.dv.apna.feature.transport.domain.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.transport.domain.model.TransportDetails
import kotlinx.coroutines.flow.Flow

interface TransportRepository {
    fun getTransportByCategory(villageId: String, categoryId: String): Flow<Resource<List<TransportDetails>>>
}
