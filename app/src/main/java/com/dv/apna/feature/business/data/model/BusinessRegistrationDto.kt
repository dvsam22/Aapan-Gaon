package com.dv.apna.feature.business.data.model

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class BusinessRegistrationDto(
    val contactName: String = "",
    val businessName: String = "",
    val category: String = "",
    val mainCategory: String? = null,
    val subCategory: String? = null,
    val phoneNumber: String = "",
    val address: String = "",
    val description: String = "",
    val villageId: String? = null,
    val villageName: String? = null,
    val status: String = "pending"
) {
    fun toMap(): Map<String, Any?> = buildMap {
        put("contactName", contactName)
        put("businessName", businessName)
        put("category", category)
        if (!mainCategory.isNullOrBlank()) put("mainCategory", mainCategory)
        if (!subCategory.isNullOrBlank()) put("subCategory", subCategory)
        put("phoneNumber", phoneNumber)
        put("address", address)
        put("description", description)
        put("villageId", villageId)
        put("villageName", villageName)
        put("status", status)
        put("createdAt", FieldValue.serverTimestamp())
    }
}
