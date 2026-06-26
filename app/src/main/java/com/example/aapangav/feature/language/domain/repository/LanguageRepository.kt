package com.example.aapangav.feature.language.domain.repository

import com.example.aapangav.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.example.aapangav.feature.language.domain.model.LanguageModel

interface LanguageRepository {
    fun getData(): Flow<Resource<List<LanguageModel>>>
}