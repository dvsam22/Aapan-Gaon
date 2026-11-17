package com.dv.apna.feature.business.domain.model

data class BusinessRegistrationModel(
    val contactName: String,
    val businessName: String,
    val category: String,
    val mainCategory: String? = null,
    val subCategory: String? = null,
    val phoneNumber: String,
    val address: String,
    val description: String = "",
    val villageId: String? = null,
    val villageName: String? = null
)
