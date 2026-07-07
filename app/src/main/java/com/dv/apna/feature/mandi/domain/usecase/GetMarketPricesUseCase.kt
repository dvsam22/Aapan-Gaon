package com.dv.apna.feature.mandi.domain.usecase

import com.dv.apna.feature.mandi.domain.repository.MandiRepository
import javax.inject.Inject

class GetMarketPricesUseCase @Inject constructor(
    private val repository: MandiRepository
) {
    operator fun invoke(villageId: String) = repository.getMarketPrices(villageId)
}
