package com.dv.apna.feature.education.domain.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.education.domain.model.EducationDetails
import kotlinx.coroutines.flow.Flow

interface EducationRepository {
    fun getEducationDetails(villageId: String, categoryId: String): Flow<Resource<List<EducationDetails>>>
}
