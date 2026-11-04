package com.dv.apna.feature.legal_panchayat.presentation.event

sealed interface LegalPanchayatEvent {
    data object BackClick : LegalPanchayatEvent
    data object Refresh : LegalPanchayatEvent
    data class CallClick(val contact: String) : LegalPanchayatEvent
}
