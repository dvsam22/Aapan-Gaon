package com.dv.apna.feature.labour.domain.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.labour.domain.model.LabourDetails
import kotlinx.coroutines.flow.Flow

interface LabourRepository {
    fun getLabours(villageId: String, categoryId: String): Flow<Resource<List<LabourDetails>>>
}
