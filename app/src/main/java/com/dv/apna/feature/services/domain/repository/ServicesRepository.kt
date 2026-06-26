package com.dv.apna.feature.services.domain.repository

import com.dv.apna.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.dv.apna.feature.services.domain.model.ServicesModel

interface ServicesRepository {
    fun getData(): Flow<Resource<List<ServicesModel>>>
}