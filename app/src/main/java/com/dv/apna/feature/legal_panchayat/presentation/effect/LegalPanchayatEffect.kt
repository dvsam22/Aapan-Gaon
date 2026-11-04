package com.dv.apna.feature.legal_panchayat.presentation.effect

sealed interface LegalPanchayatEffect {
    data object NavigateBack : LegalPanchayatEffect
    data class DialPhone(val contact: String) : LegalPanchayatEffect
}
