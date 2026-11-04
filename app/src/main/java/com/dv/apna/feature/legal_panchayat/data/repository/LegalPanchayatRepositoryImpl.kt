package com.dv.apna.feature.legal_panchayat.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.legal_panchayat.data.datasource.LegalPanchayatDataSource
import com.dv.apna.feature.legal_panchayat.data.mapper.toDomain
import com.dv.apna.feature.legal_panchayat.domain.model.LegalPanchayatDetails
import com.dv.apna.feature.legal_panchayat.domain.repository.LegalPanchayatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LegalPanchayatRepositoryImpl @Inject constructor(
    private val dataSource: LegalPanchayatDataSource,
    private val preferenceManager: PreferenceManager
) : LegalPanchayatRepository {

    override fun getLegalPanchayatDetails(villageId: String, categoryId: String): Flow<Resource<List<LegalPanchayatDetails>>> = flow {
        emit(Resource.Loading())
        try {
            val languageCode = preferenceManager.languageCode.firstOrNull() ?: "hi"
            val rawList = dataSource.getLegalPanchayatData(villageId, categoryId)
            val domainList = rawList.map { it.toDomain(languageCode) }
            emit(Resource.Success(domainList))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unexpected error occurred"))
        }
    }
}
