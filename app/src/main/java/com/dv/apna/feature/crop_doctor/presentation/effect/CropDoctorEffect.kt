package com.dv.apna.feature.crop_doctor.presentation.effect

sealed interface CropDoctorEffect {
    data object NavigateBack : CropDoctorEffect
    data class ShowToast(val message: String) : CropDoctorEffect
}
