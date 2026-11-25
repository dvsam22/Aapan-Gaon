package com.dv.apna.feature.crop_doctor.presentation.util

import android.content.Context
import android.content.Intent
import com.dv.apna.feature.crop_doctor.domain.model.CropDiagnosisResult

object CropDoctorShareHelper {

    fun formatReportShareText(
        result: CropDiagnosisResult,
        packageName: String,
        isHindi: Boolean = true
    ): String {
        val appLink = "https://play.google.com/store/apps/details?id=$packageName"
        val statusText = if (result.isHealthy) {
            if (isHindi) "✅ पौधा स्वस्थ है" else "✅ Healthy Plant"
        } else {
            if (isHindi) "⚠️ रोग: ${result.diseaseName}" else "⚠️ Issue: ${result.diseaseName}"
        }

        val symptomsHeader = if (isHindi) "🔍 *लक्षण:*" else "🔍 *Symptoms:*"
        val symptomsText = if (result.symptoms.isNotEmpty()) {
            "\n$symptomsHeader\n" + result.symptoms.joinToString("\n") { "• $it" }
        } else ""

        val treatmentText = buildString {
            if (result.chemicalTreatment.isNotBlank()) {
                val label = if (isHindi) "🧪 *रासायनिक उपचार:*" else "🧪 *Chemical Treatment:*"
                append("\n$label\n${result.chemicalTreatment}\n")
            }
            if (result.organicTreatment.isNotBlank()) {
                val label = if (isHindi) "🍃 *देसी/जैविक उपाय:*" else "🍃 *Organic / Home Remedies:*"
                append("\n$label\n${result.organicTreatment}\n")
            }
            if (result.precautions.isNotBlank()) {
                val label = if (isHindi) "⚠️ *सावधानी:*" else "⚠️ *Precautions:*"
                append("\n$label\n${result.precautions}\n")
            }
        }

        return if (isHindi) {
            """
🌿 *फसल डॉक्टर जांच रिपोर्ट (अपना गाँव)* 🌿
🌾 *फसल:* ${result.cropName}
📌 *स्थिति:* $statusText
$symptomsText$treatmentText
📲 *अपनी फसल की मुफ़्त AI जांच करने के लिए 'अपना गाँव' ऐप डाउनलोड करें:*
$appLink
            """.trimIndent()
        } else {
            """
🌿 *Crop Doctor Diagnosis Report (Apna Gaon)* 🌿
🌾 *Crop:* ${result.cropName}
📌 *Status:* $statusText
$symptomsText$treatmentText
📲 *Download 'Apna Gaon' app for free AI crop diagnosis:*
$appLink
            """.trimIndent()
        }
    }

    fun shareText(context: Context, text: String, isHindi: Boolean = true) {
        try {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            val chooserTitle = if (isHindi) "फसल रिपोर्ट शेयर करें" else "Share Crop Report"
            val shareIntent = Intent.createChooser(sendIntent, chooserTitle).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(shareIntent)
        } catch (_: Exception) {}
    }
}
