package com.dv.apna.feature.home.domain.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.home.domain.model.BannerModel
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getBanners(villageId: String): Flow<Resource<List<BannerModel>>>
}
