package com.dv.apna.feature.health.domain.repository

import com.dv.apna.core.common.Resource
import com.dv.apna.feature.health.domain.model.DoctorModel
import com.dv.apna.feature.health.domain.model.HospitalModel
import com.dv.apna.feature.health.domain.model.PharmacyModel
import kotlinx.coroutines.flow.Flow

interface HealthRepository {
    fun getDoctors(villageId: String): Flow<Resource<List<DoctorModel>>>
    fun getHospitals(villageId: String): Flow<Resource<List<HospitalModel>>>
    fun getPharmacies(villageId: String): Flow<Resource<List<PharmacyModel>>>
    fun getAmbulances(villageId: String): Flow<Resource<List<DoctorModel>>>
    fun getPolice(villageId: String): Flow<Resource<List<DoctorModel>>>
}
