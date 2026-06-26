package com.example.aapangav.feature.splash.domain.repository

import com.example.aapangav.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.example.aapangav.feature.splash.domain.model.SplashModel

interface SplashRepository {
    fun getData(): Flow<Resource<List<SplashModel>>>
}