package com.dv.apna.feature.education.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.education.data.datasource.EducationDataSource
import com.dv.apna.feature.education.data.mapper.toDomain
import com.dv.apna.feature.education.domain.model.EducationItemModel
import com.dv.apna.feature.education.domain.repository.EducationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EducationRepositoryImpl @Inject constructor(
    private val dataSource: EducationDataSource,
    private val preferenceManager: PreferenceManager
) : EducationRepository {

    override fun getEducationItems(villageId: String, categoryId: String): Flow<Resource<List<EducationItemModel>>> = flow {
        emit(Resource.Loading())
        try {
            val languageCode = preferenceManager.languageCode.firstOrNull() ?: "hi"
            val dtos = dataSource.getEducationItems(villageId, categoryId)
            val domainItems = dtos.map { it.toDomain(languageCode) }
            emit(Resource.Success(domainItems))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to load education items"))
        }
    }
}
