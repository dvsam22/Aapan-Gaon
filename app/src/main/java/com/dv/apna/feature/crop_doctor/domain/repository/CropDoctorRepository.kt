package com.dv.apna.feature.crop_doctor.domain.repository

import android.graphics.Bitmap
import com.dv.apna.core.common.Resource
import com.dv.apna.feature.crop_doctor.domain.model.CropDiagnosisResult
import kotlinx.coroutines.flow.Flow

interface CropDoctorRepository {
    fun diagnoseCrop(bitmap: Bitmap): Flow<Resource<CropDiagnosisResult>>
}
