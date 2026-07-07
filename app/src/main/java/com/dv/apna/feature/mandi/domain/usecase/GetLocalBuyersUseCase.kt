package com.dv.apna.feature.mandi.domain.usecase

import com.dv.apna.feature.mandi.domain.repository.MandiRepository
import javax.inject.Inject

class GetLocalBuyersUseCase @Inject constructor(
    private val repository: MandiRepository
) {
    operator fun invoke(villageId: String) = repository.getLocalBuyers(villageId)
}
