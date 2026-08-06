package com.dv.apna.feature.language.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.language.data.datasource.LanguageDataSource
import com.dv.apna.feature.language.data.mapper.toDomain
import com.dv.apna.feature.language.domain.model.LanguageModel
import com.dv.apna.feature.language.domain.model.VillageModel
import com.dv.apna.feature.language.domain.repository.LanguageRepository
import android.util.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getVillages(): Flow<Resource<List<VillageModel>>> = preferenceManager.languageCode.flatMapLatest { languageCode ->
        flow {
            emit(Resource.Loading())
            try {
                val currentLang = languageCode ?: "en"
                Log.d("LanguageRepo", "Fetching villages for language: $currentLang")
                val villagesDto = dataSource.getVillages()
                Log.d("LanguageRepo", "Fetched ${villagesDto.size} villages from DataSource")
                val activeVillages = villagesDto.filter { it.isCurrentlyActive }
                Log.d("LanguageRepo", "Filtered ${activeVillages.size} active villages")
                val villages = activeVillages.map { it.toDomain(currentLang) }
                emit(Resource.Success(villages))
            } catch (e: Exception) {
                Log.e("LanguageRepo", "Error fetching villages: ${e.message}", e)
                emit(Resource.Error(e.message ?: "Unknown Error"))
            }
        }
    }

    override suspend fun saveVillage(id: String, name: String, lat: Double, lng: Double) {
        preferenceManager.saveVillage(id, name, lat, lng)
    }

    override suspend fun saveLanguage(code: String) {
        preferenceManager.saveLanguage(code)
    }
}
