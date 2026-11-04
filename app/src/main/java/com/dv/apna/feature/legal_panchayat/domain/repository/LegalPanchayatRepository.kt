package com.dv.apna.feature.legal_panchayat.domain.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.legal_panchayat.domain.model.LegalPanchayatDetails
import kotlinx.coroutines.flow.Flow

interface LegalPanchayatRepository {
    fun getLegalPanchayatDetails(villageId: String, categoryId: String): Flow<Resource<List<LegalPanchayatDetails>>>
}
