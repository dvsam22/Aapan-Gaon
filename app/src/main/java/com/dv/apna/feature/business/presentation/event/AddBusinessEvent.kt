package com.dv.apna.feature.business.presentation.event

sealed interface AddBusinessEvent {
    data class ContactNameChanged(val name: String) : AddBusinessEvent
    data class BusinessNameChanged(val businessName: String) : AddBusinessEvent
    data class CategorySelected(val category: String) : AddBusinessEvent
    data class PhoneNumberChanged(val phone: String) : AddBusinessEvent
    data class AddressChanged(val address: String) : AddBusinessEvent
    data class DescriptionChanged(val description: String) : AddBusinessEvent
    data object Submit : AddBusinessEvent
    data object ResetForm : AddBusinessEvent
}
