package com.dv.apna.feature.dignitaries.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.dignitaries.data.datasource.DignitariesDataSource
import com.dv.apna.feature.dignitaries.data.mapper.toDomain
import com.dv.apna.feature.dignitaries.domain.model.DignitaryItemModel
import com.dv.apna.feature.dignitaries.domain.repository.DignitariesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DignitariesRepositoryImpl @Inject constructor(
    private val dataSource: DignitariesDataSource,
    private val preferenceManager: PreferenceManager
) : DignitariesRepository {

    override fun getDignitaries(villageId: String, categoryId: String): Flow<Resource<List<DignitaryItemModel>>> = flow {
        emit(Resource.Loading())
        try {
            val languageCode = preferenceManager.languageCode.firstOrNull() ?: "hi"
            val dtos = dataSource.getDignitaries(villageId, categoryId)
            val domainItems = dtos.map { it.toDomain(languageCode) }
            emit(Resource.Success(domainItems))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to load dignitaries"))
        }
    }
}
