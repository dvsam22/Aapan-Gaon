package com.dv.apna.feature.mandi.domain.repository

import com.dv.apna.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.dv.apna.feature.mandi.domain.model.MandiModel

interface MandiRepository {
    fun getData(): Flow<Resource<List<MandiModel>>>
}