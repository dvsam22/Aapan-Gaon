package com.dv.apna.feature.labour.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.labour.data.datasource.LabourDataSource
import com.dv.apna.feature.labour.data.mapper.toDomain
import com.dv.apna.feature.labour.domain.model.LabourDetails
import com.dv.apna.feature.labour.domain.repository.LabourRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LabourRepositoryImpl @Inject constructor(
    private val dataSource: LabourDataSource,
    private val preferenceManager: PreferenceManager
) : LabourRepository {
    override fun getLabours(villageId: String, categoryId: String): Flow<Resource<List<LabourDetails>>> = flow {
        emit(Resource.Loading())
        try {
            val languageCode = preferenceManager.languageCode.firstOrNull() ?: "en"
            val labors = dataSource.getLabours(villageId, categoryId).map { it.toDomain(languageCode) }
            emit(Resource.Success(labors))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }
}
