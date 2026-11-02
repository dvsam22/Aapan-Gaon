package com.dv.apna.feature.dignitaries.domain.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.dignitaries.domain.model.DignitaryItemModel
import kotlinx.coroutines.flow.Flow

interface DignitariesRepository {
    fun getDignitaries(villageId: String, categoryId: String): Flow<Resource<List<DignitaryItemModel>>>
}
