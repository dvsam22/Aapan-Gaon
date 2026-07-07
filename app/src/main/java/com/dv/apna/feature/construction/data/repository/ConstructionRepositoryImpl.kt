package com.dv.apna.feature.construction.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.construction.data.datasource.ConstructionDataSource
import com.dv.apna.feature.construction.data.mapper.toBricksSupplier
import com.dv.apna.feature.construction.data.mapper.toHardwareShop
import com.dv.apna.feature.construction.data.mapper.toMaterialShop
import com.dv.apna.feature.construction.domain.model.BricksSupplierModel
import com.dv.apna.feature.construction.domain.model.HardwareShopModel
import com.dv.apna.feature.construction.domain.model.MaterialShopModel
import com.dv.apna.feature.construction.domain.repository.ConstructionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ConstructionRepositoryImpl @Inject constructor(
    private val dataSource: ConstructionDataSource
) : ConstructionRepository {

    override fun getBricksSuppliers(villageId: String): Flow<Resource<List<BricksSupplierModel>>> = flow {
        emit(Resource.Loading())
        try {
            val data = dataSource.getConstructionData(villageId, "bricks")
                .map { it.toBricksSupplier() }
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    override fun getMaterialShops(villageId: String): Flow<Resource<List<MaterialShopModel>>> = flow {
        emit(Resource.Loading())
        try {
            val data = dataSource.getConstructionData(villageId, "material_shops")
                .map { it.toMaterialShop() }
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    override fun getHardwareShops(villageId: String): Flow<Resource<List<HardwareShopModel>>> = flow {
        emit(Resource.Loading())
        try {
            val data = dataSource.getConstructionData(villageId, "hardware_shops")
                .map { it.toHardwareShop() }
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }
}
