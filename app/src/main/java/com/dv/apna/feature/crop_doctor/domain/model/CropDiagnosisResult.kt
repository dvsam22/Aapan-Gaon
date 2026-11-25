package com.dv.apna.feature.crop_doctor.domain.model

data class CropDiagnosisResult(
    val isPlant: Boolean = true,
    val cropName: String = "",
    val diseaseName: String = "",
    val isHealthy: Boolean = false,
    val severity: String = "Medium", // Low, Medium, High, None
    val symptoms: List<String> = emptyList(),
    val chemicalTreatment: String = "",
    val organicTreatment: String = "",
    val precautions: String = "",
    val generalAdvice: String = ""
)
