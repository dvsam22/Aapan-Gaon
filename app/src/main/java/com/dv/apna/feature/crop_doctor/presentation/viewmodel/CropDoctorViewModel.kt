package com.dv.apna.feature.crop_doctor.presentation.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.core.common.Resource
import com.dv.apna.core.common.UiText
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.crop_doctor.domain.repository.CropDoctorRepository
import com.dv.apna.feature.crop_doctor.presentation.effect.CropDoctorEffect
import com.dv.apna.feature.crop_doctor.presentation.event.CropDoctorEvent
import com.dv.apna.feature.crop_doctor.presentation.state.CropDoctorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CropDoctorViewModel @Inject constructor(
    application: Application,
    private val repository: CropDoctorRepository,
    private val preferenceManager: PreferenceManager
) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val _state = MutableStateFlow(CropDoctorState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<CropDoctorEffect>()
    val effect = _effect.asSharedFlow()

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private var currentLanguageCode: String = "en"

    init {
        viewModelScope.launch {
            preferenceManager.languageCode.collect { lang ->
                currentLanguageCode = lang ?: "en"
            }
        }
        try {
            tts = TextToSpeech(application, this)
        } catch (e: Exception) {
            android.util.Log.w("CropDoctorVM", "TTS init error: ${e.message}")
        }
    }

    private fun isHindiLanguage(): Boolean = currentLanguageCode.equals("hi", ignoreCase = true)

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val isHindi = isHindiLanguage()
            val targetLocale = if (isHindi) Locale("hi", "IN") else Locale("en", "IN")
            val result = tts?.setLanguage(targetLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(if (isHindi) Locale.getDefault() else Locale.US)
            }
            isTtsInitialized = true

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _state.update { it.copy(isSpeaking = true) }
                }

                override fun onDone(utteranceId: String?) {
                    _state.update { it.copy(isSpeaking = false) }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _state.update { it.copy(isSpeaking = false) }
                }
            })
        }
    }

    fun onEvent(event: CropDoctorEvent) {
        when (event) {
            is CropDoctorEvent.ImageSelected -> {
                stopSpeech()
                val loadErrorMsg = if (isHindiLanguage()) {
                    "फ़ोटो लोड नहीं हो सकी, कृपया दोबारा प्रयास करें।"
                } else {
                    "Could not load photo, please try again."
                }
                _state.update {
                    it.copy(
                        selectedImageUri = event.uri,
                        selectedBitmap = event.bitmap,
                        error = if (event.bitmap == null) UiText.DynamicString(loadErrorMsg) else null
                    )
                }
                event.bitmap?.let { bitmap ->
                    diagnose(bitmap)
                }
            }
            is CropDoctorEvent.ToggleSpeech -> {
                toggleSpeech()
            }
            is CropDoctorEvent.StopSpeech -> {
                stopSpeech()
            }
            is CropDoctorEvent.ResetScan -> {
                stopSpeech()
                _state.update {
                    CropDoctorState()
                }
            }
            is CropDoctorEvent.BackClick -> {
                stopSpeech()
                viewModelScope.launch {
                    _effect.emit(CropDoctorEffect.NavigateBack)
                }
            }
        }
    }

    private fun diagnose(bitmap: Bitmap) {
        repository.diagnoseCrop(bitmap).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true, error = null) }
                }
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            diagnosisResult = result.data,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    val fallbackErr = if (isHindiLanguage()) {
                        "जांच विफल रही, पुनः प्रयास करें"
                    } else {
                        "Diagnosis failed, please try again."
                    }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = UiText.DynamicString(result.message ?: fallbackErr)
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun toggleSpeech() {
        if (_state.value.isSpeaking) {
            stopSpeech()
            return
        }

        val diagnosis = _state.value.diagnosisResult ?: return
        if (!diagnosis.isPlant) return

        val isHindi = isHindiLanguage()
        if (!isTtsInitialized || tts == null) {
            viewModelScope.launch {
                val msg = if (isHindi) {
                    "आवाज़ की सुविधा अभी तैयार नहीं है"
                } else {
                    "Voice playback is not ready yet"
                }
                _effect.emit(CropDoctorEffect.ShowToast(msg))
            }
            return
        }

        val targetLocale = if (isHindi) Locale("hi", "IN") else Locale("en", "IN")
        val langResult = tts?.setLanguage(targetLocale)
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.setLanguage(if (isHindi) Locale.getDefault() else Locale.US)
        }

        val speechText = buildString {
            if (isHindi) {
                append("${diagnosis.cropName} की जांच रिपोर्ट। ")
                if (diagnosis.isHealthy) {
                    append("स्थिति: पौधा स्वस्थ है। ")
                } else {
                    append("पहचाना गया रोग: ${diagnosis.diseaseName}। ")
                }
                if (diagnosis.symptoms.isNotEmpty()) {
                    append("मुख्य लक्षण: ${diagnosis.symptoms.joinToString("। ")}। ")
                }
                if (diagnosis.chemicalTreatment.isNotBlank()) {
                    append("रासायनिक दवा व मात्रा: ${diagnosis.chemicalTreatment}। ")
                }
                if (diagnosis.organicTreatment.isNotBlank()) {
                    append("देसी व जैविक उपाय: ${diagnosis.organicTreatment}। ")
                }
                if (diagnosis.precautions.isNotBlank()) {
                    append("सावधानियां: ${diagnosis.precautions}। ")
                }
                if (diagnosis.generalAdvice.isNotBlank()) {
                    append("सलाह: ${diagnosis.generalAdvice}।")
                }
            } else {
                append("Crop diagnosis report for ${diagnosis.cropName}. ")
                if (diagnosis.isHealthy) {
                    append("Status: Healthy plant. ")
                } else {
                    append("Detected issue: ${diagnosis.diseaseName}. ")
                }
                if (diagnosis.symptoms.isNotEmpty()) {
                    append("Main symptoms: ${diagnosis.symptoms.joinToString(". ")}. ")
                }
                if (diagnosis.chemicalTreatment.isNotBlank()) {
                    append("Chemical medicine and dosage: ${diagnosis.chemicalTreatment}. ")
                }
                if (diagnosis.organicTreatment.isNotBlank()) {
                    append("Organic or home remedies: ${diagnosis.organicTreatment}. ")
                }
                if (diagnosis.precautions.isNotBlank()) {
                    append("Precautions: ${diagnosis.precautions}. ")
                }
                if (diagnosis.generalAdvice.isNotBlank()) {
                    append("Advice: ${diagnosis.generalAdvice}.")
                }
            }
        }

        tts?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "crop_diagnosis_utterance")
        _state.update { it.copy(isSpeaking = true) }
    }

    private fun stopSpeech() {
        tts?.stop()
        _state.update { it.copy(isSpeaking = false) }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            android.util.Log.w("CropDoctorVM", "TTS shutdown error: ${e.message}")
        }
    }
}
