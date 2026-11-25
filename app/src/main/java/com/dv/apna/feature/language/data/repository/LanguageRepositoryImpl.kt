package com.dv.apna.feature.language.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.language.data.datasource.LanguageDataSource
import com.dv.apna.feature.language.data.mapper.toDomain
import com.dv.apna.feature.language.domain.model.LanguageModel
import com.dv.apna.feature.language.domain.model.VillageModel
import com.dv.apna.feature.language.domain.repository.LanguageRepository
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
                val blocksDto = dataSource.getBlocks()
                val activeBlocks = blocksDto.filter { it.isCurrentlyActive }
                val blocks = if (activeBlocks.isNotEmpty()) {
                    activeBlocks.map { it.toDomain(currentLang) }
                } else {
                    listOf(defaultPaniyaraBlock(currentLang))
                }
                emit(Resource.Success(blocks))
            } catch (_: Exception) {
                val currentLang = languageCode ?: "en"
                emit(Resource.Success(listOf(defaultPaniyaraBlock(currentLang))))
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getVillageDirectory(): Flow<Resource<List<VillageModel>>> = preferenceManager.languageCode.flatMapLatest { languageCode ->
        flow {
            emit(Resource.Loading())
            try {
                val currentLang = languageCode ?: "en"
                val villagesDto = dataSource.getVillages()
                val activeVillages = villagesDto.filter { it.isCurrentlyActive }
                val villages = activeVillages.map { it.toDomain(currentLang) }
                emit(Resource.Success(villages))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unknown Error"))
            }
        }
    }

    private fun defaultPaniyaraBlock(languageCode: String): VillageModel {
        val isHi = languageCode.equals("hi", ignoreCase = true)
        return VillageModel(
            id = PreferenceManager.DEFAULT_BLOCK_ID,
            villageName = PreferenceManager.defaultBlockName(languageCode),
            district = if (isHi) "महराजगंज" else "Maharajganj",
            state = if (isHi) "उत्तर प्रदेश" else "Uttar Pradesh",
            pincode = "273310",
            image = "",
            lat = 27.00,
            lng = 83.42,
            sarpanchName = "",
            sarpanchPhone = ""
        )
    }

    override suspend fun saveVillage(id: String, name: String, lat: Double, lng: Double) {
        preferenceManager.saveVillage(id, name, lat, lng)
    }

    override suspend fun saveLanguage(code: String) {
        preferenceManager.saveLanguage(code)
    }
}
