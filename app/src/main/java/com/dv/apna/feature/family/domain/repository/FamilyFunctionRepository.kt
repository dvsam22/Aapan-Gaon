package com.dv.apna.feature.family.domain.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.family.domain.model.FamilyFunctionDetails
import kotlinx.coroutines.flow.Flow

interface FamilyFunctionRepository {
    fun getFamilyFunctions(villageId: String, categoryId: String): Flow<Resource<List<FamilyFunctionDetails>>>
}
