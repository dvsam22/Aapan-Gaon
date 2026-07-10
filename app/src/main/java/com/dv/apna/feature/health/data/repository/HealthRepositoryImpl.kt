package com.dv.apna.feature.health.data.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.health.data.datasource.HealthDataSource
import com.dv.apna.feature.health.data.mapper.toDoctorModel
import com.dv.apna.feature.health.data.mapper.toGenericModel
import com.dv.apna.feature.health.data.mapper.toHospitalModel
import com.dv.apna.feature.health.data.mapper.toPharmacyModel
import com.dv.apna.feature.health.domain.model.DoctorModel
import com.dv.apna.feature.health.domain.model.HospitalModel
import com.dv.apna.feature.health.domain.model.PharmacyModel
import com.dv.apna.feature.health.domain.repository.HealthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HealthRepositoryImpl @Inject constructor(
    private val dataSource: HealthDataSource,
    private val preferenceManager: PreferenceManager
) : HealthRepository {

    override fun getDoctors(villageId: String): Flow<Resource<List<DoctorModel>>> = flow {
        emit(Resource.Loading())
        try {
            val languageCode = preferenceManager.languageCode.firstOrNull() ?: "en"
            val data = dataSource.getHealthData(villageId, "doctors")
                .map { it.toDoctorModel(languageCode) }
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    override fun getHospitals(villageId: String): Flow<Resource<List<HospitalModel>>> = flow {
        emit(Resource.Loading())
        try {
            val languageCode = preferenceManager.languageCode.firstOrNull() ?: "en"
            val data = dataSource.getHealthData(villageId, "hospitals")
                .map { it.toHospitalModel(languageCode) }
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    override fun getPharmacies(villageId: String): Flow<Resource<List<PharmacyModel>>> = flow {
        emit(Resource.Loading())
        try {
            val languageCode = preferenceManager.languageCode.firstOrNull() ?: "en"
            val data = dataSource.getHealthData(villageId, "pharmacy")
                .map { it.toPharmacyModel(languageCode) }
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    override fun getAmbulances(villageId: String): Flow<Resource<List<DoctorModel>>> = flow {
        emit(Resource.Loading())
        try {
            val languageCode = preferenceManager.languageCode.firstOrNull() ?: "en"
            val data = dataSource.getHealthData(villageId, "ambulance")
                .map { it.toGenericModel(languageCode) }
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    override fun getPolice(villageId: String): Flow<Resource<List<DoctorModel>>> = flow {
        emit(Resource.Loading())
        try {
            val languageCode = preferenceManager.languageCode.firstOrNull() ?: "en"
            val data = dataSource.getHealthData(villageId, "police")
                .map { it.toGenericModel(languageCode) }
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }
}
