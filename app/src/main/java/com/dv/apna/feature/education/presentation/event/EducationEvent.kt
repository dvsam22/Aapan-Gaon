package com.dv.apna.feature.education.presentation.event

sealed interface EducationEvent {
    data class LoadCategory(val categoryId: String, val categoryName: String) : EducationEvent
    data object Refresh : EducationEvent
    data object BackClicked : EducationEvent
    data class CallClicked(val contact: String) : EducationEvent
    data class WhatsAppClicked(val phone: String) : EducationEvent
    data class OpenLink(val url: String) : EducationEvent
}
