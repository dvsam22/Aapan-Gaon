package com.dv.apna.feature.language.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.language.data.datasource.LanguageDataSource
import com.dv.apna.feature.language.data.mapper.toDomain
import com.dv.apna.feature.language.domain.model.LanguageModel
import com.dv.apna.feature.language.domain.model.VillageModel
import com.dv.apna.feature.language.domain.repository.LanguageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LanguageRepositoryImpl @Inject constructor(
    private val dataSource: LanguageDataSource,
    private val preferenceManager: PreferenceManager
) : LanguageRepository {

    override fun getLanguages(): Flow<Resource<List<LanguageModel>>> = flow {
        emit(Resource.Loading())
        // Dummy for now or fetch from some source
        emit(Resource.Success(listOf(
            LanguageModel("1", "English", "English", "en"),
            LanguageModel("2", "Hindi", "हिंदी", "hi")
        )))
    }

    override fun getVillages(): Flow<Resource<List<VillageModel>>> = flow {
        emit(Resource.Loading())
        try {
            val villages = dataSource.getVillages().map { it.toDomain() }
            emit(Resource.Success(villages))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown Error"))
        }
    }

    override suspend fun saveVillage(id: String, name: String) {
        preferenceManager.saveVillage(id, name)
    }

    override suspend fun saveLanguage(code: String) {
        preferenceManager.saveLanguage(code)
    }
}
