package com.dv.apna.feature.business.data.model

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class BusinessRegistrationDto(
    val contactName: String = "",
    val businessName: String = "",
    val category: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val description: String = "",
    val villageId: String? = null,
    val villageName: String? = null,
    val status: String = "pending"
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "contactName" to contactName,
        "businessName" to businessName,
        "category" to category,
        "phoneNumber" to phoneNumber,
        "address" to address,
        "description" to description,
        "villageId" to villageId,
        "villageName" to villageName,
        "status" to status,
        "createdAt" to FieldValue.serverTimestamp()
    )
}
