package com.dv.apna.feature.home.domain.repository

import com.dv.apna.feature.home.domain.model.HomeModel
import com.dv.apna.core.common.Resource
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getHomeData(): Flow<Resource<List<HomeModel>>>
}
