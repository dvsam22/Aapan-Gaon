package com.dv.apna.feature.splash.domain.repository

import com.dv.apna.core.common.Resource
import kotlinx.coroutines.flow.Flow
import com.dv.apna.feature.splash.domain.model.SplashModel

interface SplashRepository {
    fun getData(): Flow<Resource<List<SplashModel>>>
}