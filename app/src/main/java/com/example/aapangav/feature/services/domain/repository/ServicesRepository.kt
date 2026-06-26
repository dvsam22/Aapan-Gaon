package com.example.aapangav.feature.services.domain.repository

import com.example.aapangav.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.example.aapangav.feature.services.domain.model.ServicesModel

interface ServicesRepository {
    fun getData(): Flow<Resource<List<ServicesModel>>>
}