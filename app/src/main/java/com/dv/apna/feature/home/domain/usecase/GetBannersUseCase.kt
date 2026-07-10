package com.dv.apna.feature.home.domain.usecase

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.home.domain.model.BannerModel
import com.dv.apna.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBannersUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(villageId: String): Flow<Resource<List<BannerModel>>> {
        return repository.getBanners(villageId)
    }
}
