package com.dv.apna.feature.language.domain.repository

import com.dv.apna.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.dv.apna.feature.language.domain.model.LanguageModel

interface LanguageRepository {
    fun getData(): Flow<Resource<List<LanguageModel>>>
}