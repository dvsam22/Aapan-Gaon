package com.dv.apna.feature.construction.domain.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.construction.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ConstructionRepository {
    fun getBricksSuppliers(villageId: String): Flow<Resource<List<BricksSupplierModel>>>
    fun getMaterialShops(villageId: String): Flow<Resource<List<MaterialShopModel>>>
    fun getHardwareShops(villageId: String): Flow<Resource<List<HardwareShopModel>>>
}
