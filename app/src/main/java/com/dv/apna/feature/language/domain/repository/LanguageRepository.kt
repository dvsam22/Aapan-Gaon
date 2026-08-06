package com.dv.apna.feature.language.domain.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.language.domain.model.LanguageModel
import com.dv.apna.feature.language.domain.model.VillageModel
import kotlinx.coroutines.flow.Flow

interface LanguageRepository {
    fun getLanguages(): Flow<Resource<List<LanguageModel>>>
    fun getVillages(): Flow<Resource<List<VillageModel>>>
    suspend fun saveVillage(id: String, name: String, lat: Double = 0.0, lng: Double = 0.0)
    suspend fun saveLanguage(code: String)
}
