package com.dv.apna.feature.family.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.family.data.datasource.FamilyFunctionDataSource
import com.dv.apna.feature.family.data.mapper.toDomain
import com.dv.apna.feature.family.domain.model.FamilyFunctionDetails
import com.dv.apna.feature.family.domain.repository.FamilyFunctionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FamilyFunctionRepositoryImpl @Inject constructor(
    private val dataSource: FamilyFunctionDataSource,
    private val preferenceManager: PreferenceManager
) : FamilyFunctionRepository {
    override fun getFamilyFunctions(villageId: String, categoryId: String): Flow<Resource<List<FamilyFunctionDetails>>> = flow {
        emit(Resource.Loading())
        try {
            val languageCode = preferenceManager.languageCode.firstOrNull() ?: "en"
            val functions = dataSource.getFamilyFunctions(villageId, categoryId).map { it.toDomain(languageCode) }
            emit(Resource.Success(functions))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }
}
