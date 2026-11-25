package com.dv.apna.feature.crop_doctor.presentation.event

import android.graphics.Bitmap
import android.net.Uri

sealed interface CropDoctorEvent {
    data class ImageSelected(val uri: Uri?, val bitmap: Bitmap?) : CropDoctorEvent
    data object ToggleSpeech : CropDoctorEvent
    data object StopSpeech : CropDoctorEvent
    data object ResetScan : CropDoctorEvent
    data object BackClick : CropDoctorEvent
}
