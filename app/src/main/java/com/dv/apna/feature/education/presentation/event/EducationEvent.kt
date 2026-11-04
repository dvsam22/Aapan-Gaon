package com.dv.apna.feature.education.presentation.event

sealed interface EducationEvent {
    data object BackClick : EducationEvent
    data object Refresh : EducationEvent
    data class CallClick(val contact: String) : EducationEvent
}
