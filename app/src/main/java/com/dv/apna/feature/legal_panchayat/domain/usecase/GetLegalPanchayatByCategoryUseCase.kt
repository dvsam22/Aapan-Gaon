package com.dv.apna.feature.legal_panchayat.domain.usecase

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.legal_panchayat.domain.model.LegalPanchayatDetails
import com.dv.apna.feature.legal_panchayat.domain.repository.LegalPanchayatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLegalPanchayatByCategoryUseCase @Inject constructor(
    private val repository: LegalPanchayatRepository
) {
    operator fun invoke(villageId: String, categoryId: String): Flow<Resource<List<LegalPanchayatDetails>>> {
        return repository.getLegalPanchayatDetails(villageId, categoryId)
    }
}
