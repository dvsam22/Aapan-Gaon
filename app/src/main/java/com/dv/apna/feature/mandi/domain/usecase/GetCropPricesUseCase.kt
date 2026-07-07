package com.dv.apna.feature.mandi.domain.usecase

import com.dv.apna.feature.mandi.domain.repository.MandiRepository
import javax.inject.Inject

class GetCropPricesUseCase @Inject constructor(
    private val repository: MandiRepository
) {
    operator fun invoke(villageId: String) = repository.getCropPrices(villageId)
}
