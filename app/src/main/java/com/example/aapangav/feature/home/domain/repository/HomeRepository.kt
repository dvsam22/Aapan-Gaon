package com.example.aapangav.feature.home.domain.repository

import com.example.aapangav.feature.home.domain.model.HomeModel
import com.example.aapangav.core.common.Resource
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getHomeData(): Flow<Resource<List<HomeModel>>>
}
