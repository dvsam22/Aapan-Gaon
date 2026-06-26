package com.example.aapangav.feature.language.data.repository

import com.example.aapangav.core.common.Resource
import com.example.aapangav.feature.language.domain.model.LanguageModel
import com.example.aapangav.feature.language.domain.repository.LanguageRepository
import com.example.aapangav.feature.language.data.datasource.LanguageDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LanguageRepositoryImpl @Inject constructor(
    private val dataSource: LanguageDataSource
) : LanguageRepository {
    override fun getData(): Flow<Resource<List<LanguageModel>>> = flow {
        emit(Resource.Loading())
    }
}