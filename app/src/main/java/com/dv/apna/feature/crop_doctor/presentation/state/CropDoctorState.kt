package com.dv.apna.feature.crop_doctor.presentation.state

import android.graphics.Bitmap
import android.net.Uri
import com.dv.apna.core.common.UiText
import com.dv.apna.feature.crop_doctor.domain.model.CropDiagnosisResult

data class CropDoctorState(
    val isLoading: Boolean = false,
    val selectedImageUri: Uri? = null,
    val selectedBitmap: Bitmap? = null,
    val diagnosisResult: CropDiagnosisResult? = null,
    val isSpeaking: Boolean = false,
    val error: UiText? = null
)
