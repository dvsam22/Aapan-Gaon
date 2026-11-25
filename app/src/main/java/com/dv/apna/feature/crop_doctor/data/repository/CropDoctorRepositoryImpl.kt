package com.dv.apna.feature.crop_doctor.data.repository

import android.graphics.Bitmap
import com.dv.apna.core.common.GeminiContentHelper
import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.crop_doctor.domain.model.CropDiagnosisResult
import com.dv.apna.feature.crop_doctor.domain.repository.CropDoctorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CropDoctorRepositoryImpl @Inject constructor(
    private val preferenceManager: PreferenceManager
) : CropDoctorRepository {

    override fun diagnoseCrop(bitmap: Bitmap): Flow<Resource<CropDiagnosisResult>> = flow {
        emit(Resource.Loading())
        val languageCode = preferenceManager.languageCode.firstOrNull() ?: "en"
        val isHindi = languageCode.equals("hi", ignoreCase = true)
        try {
            val scaledBitmap = scaleBitmap(bitmap, 512)
            val prompt = buildDiagnosisPrompt(isHindi)

            val fallbackError = if (isHindi) {
                "फसल की जांच नहीं हो सकी, कृपया दोबारा प्रयास करें।"
            } else {
                "Could not complete crop diagnosis, please try again."
            }
            val responseText = GeminiContentHelper.generateJson(
                promptText = prompt,
                bitmap = scaledBitmap,
                temperature = 0.2f,
                maxOutputTokens = 650,
                fallbackErrorMessage = fallbackError,
                logTag = "CropDoctor"
            )

            val result = parseDiagnosis(responseText, isHindi)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            android.util.Log.e("CropDoctor", "Diagnosis error", e)
            val defaultErr = if (isHindi) "जांच पूरी नहीं हो सकी।" else "Could not complete diagnosis."
            emit(Resource.Error(GeminiContentHelper.extractErrorMessage(e, defaultErr)))
        }
    }.flowOn(Dispatchers.IO)

    private fun buildDiagnosisPrompt(isHindi: Boolean): String {
        val languageRule = if (isHindi) {
            "CRITICAL LANGUAGE RULE: Respond strictly in simple, natural Hindi (Devanagari script) for ALL JSON string values."
        } else {
            "CRITICAL LANGUAGE RULE: Respond strictly in simple, clear English for ALL JSON string values. Write crop/plant names in English (e.g., \"Aglaonema\", \"Rice\", \"Wheat\", \"Tomato\"). Do NOT use Hindi Devanagari script anywhere in the JSON."
        }

        val notPlantMessage = if (isHindi) {
            "कृपया किसी पौधे, पत्ते या फसल की साफ़ फ़ोटो खींचें।"
        } else {
            "Please take a clear photo of a plant, leaf, or crop."
        }

        val schemaExample = if (isHindi) {
            """
            {
              "isPlant": true,
              "cropName": "फसल का नाम हिंदी में (जैसे: धान, आलू, टमाटर, गेहूं, सरसों)",
              "diseaseName": "रोग का नाम हिंदी में (या 'स्वस्थ पौधा' अगर कोई रोग नहीं है)",
              "isHealthy": false,
              "severity": "Medium",
              "symptoms": [
                "लक्षण 1 (हिंदी में)",
                "लक्षण 2 (हिंदी में)"
              ],
              "chemicalTreatment": "रासायनिक दवा का नाम व मात्रा (जैसे: मैनकोजेब 75% WP - 2 ग्राम प्रति लीटर पानी में; स्वस्थ पौधे हेतु: 'किसी उपचार की आवश्यकता नहीं है')",
              "organicTreatment": "देसी या जैविक उपाय (जैसे: 5ml नीम का तेल प्रति लीटर पानी में या देखभाल टिप्स)",
              "precautions": "छिड़काव या देखभाल करते समय सावधानियां (जैसे: शाम के समय छिड़काव करें, तेज धूप में न करें)",
              "generalAdvice": "किसान भाई के लिए एक पंक्ति की सलाह"
            }
            """.trimIndent()
        } else {
            """
            {
              "isPlant": true,
              "cropName": "Crop or plant name in English (e.g., Aglaonema, Rice, Tomato, Wheat)",
              "diseaseName": "Short disease or issue name in 2-5 words (or 'Healthy Plant' if healthy)",
              "isHealthy": false,
              "severity": "Medium",
              "symptoms": [
                "Symptom 1 in simple English",
                "Symptom 2 in simple English"
              ],
              "chemicalTreatment": "Chemical medicine name & dosage (e.g., Mancozeb 75% WP - 2 g per liter of water; if healthy, write 'No chemical treatment needed')",
              "organicTreatment": "Organic/natural remedy or care tips (e.g., Mix 5 ml neem oil per liter of water and spray evenly)",
              "precautions": "Important spraying or watering precautions (e.g., Spray during cool evening hours; avoid overwatering)",
              "generalAdvice": "One helpful line of practical advice in English"
            }
            """.trimIndent()
        }

        return """
            You are an expert Indian Agricultural Scientist diagnosing crop diseases and plant health for farmers and home gardeners.
            Examine this plant/leaf image very carefully.
            $languageRule

            If this image is NOT a plant, leaf, crop, fruit, or vegetable (for example, a human face, animal, vehicle, document, or random object):
            Respond with this exact JSON format:
            {
              "isPlant": false,
              "message": "$notPlantMessage"
            }

            If this IS a plant or crop:
            Diagnose the crop and any disease, nutrient deficiency, or pest attack. Keep responses concise (2-3 short symptom bullets, 1-2 brief sentences per treatment/precaution).
            Respond ONLY with a valid JSON object in this exact schema (no markdown, no extra commentary):
            $schemaExample
        """.trimIndent()
    }

    private fun extractJson(text: String): String {
        val trimmed = text.trim()
        val afterFence = if (trimmed.contains("```json")) {
            trimmed.substringAfter("```json").substringBefore("```").trim()
        } else if (trimmed.contains("```")) {
            trimmed.substringAfter("```").substringBefore("```").trim()
        } else {
            trimmed
        }

        val firstBrace = afterFence.indexOf('{')
        val lastBrace = afterFence.lastIndexOf('}')
        return if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            afterFence.substring(firstBrace, lastBrace + 1).trim()
        } else {
            afterFence
        }
    }

    private fun parseDiagnosis(responseText: String, isHindi: Boolean): CropDiagnosisResult {
        return try {
            val cleanJson = extractJson(responseText)

            val json = JSONObject(cleanJson)
            val isPlant = json.optBoolean("isPlant", true)
            if (!isPlant) {
                val notPlantFallback = if (isHindi) {
                    "कृपया किसी पौधे, पत्ते या फसल की साफ़ फ़ोटो खींचें।"
                } else {
                    "Please take a clear photo of a plant, leaf, or crop."
                }
                return CropDiagnosisResult(
                    isPlant = false,
                    generalAdvice = json.optString("message", notPlantFallback)
                )
            }

            val defaultCrop = if (isHindi) "अज्ञात फसल" else "Unknown Crop"
            val defaultDisease = if (isHindi) "रोग की पहचान जारी" else "Diagnosis Completed"
            val cropName = json.optString("cropName", defaultCrop)
            val diseaseName = json.optString("diseaseName", defaultDisease)
            val isHealthy = json.optBoolean("isHealthy", false)
            val severity = json.optString("severity", if (isHealthy) "None" else "Medium")
            val symptomsArray = json.optJSONArray("symptoms")
            val symptoms = mutableListOf<String>()
            if (symptomsArray != null) {
                for (i in 0 until symptomsArray.length()) {
                    symptoms.add(symptomsArray.optString(i))
                }
            }
            val chemicalTreatment = json.optString("chemicalTreatment", "")
            val organicTreatment = json.optString("organicTreatment", "")
            val precautions = json.optString("precautions", "")
            val generalAdvice = json.optString("generalAdvice", "")

            CropDiagnosisResult(
                isPlant = true,
                cropName = cropName,
                diseaseName = diseaseName,
                isHealthy = isHealthy,
                severity = severity,
                symptoms = symptoms,
                chemicalTreatment = chemicalTreatment,
                organicTreatment = organicTreatment,
                precautions = precautions,
                generalAdvice = generalAdvice
            )
        } catch (e: Exception) {
            // Fallback graceful degradation
            CropDiagnosisResult(
                isPlant = true,
                cropName = if (isHindi) "फसल" else "Crop",
                diseaseName = if (isHindi) "जांच परिणाम" else "Diagnosis Result",
                generalAdvice = responseText
            )
        }
    }

    private fun scaleBitmap(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) return bitmap
        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int
        if (ratio > 1f) {
            newWidth = maxDimension
            newHeight = (maxDimension / ratio).toInt().coerceAtLeast(1)
        } else {
            newHeight = maxDimension
            newWidth = (maxDimension * ratio).toInt().coerceAtLeast(1)
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
