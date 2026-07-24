package com.dv.apna.feature.language.data.model

import com.google.firebase.firestore.PropertyName

data class VillageDto(
    val id: String? = null,
    val villageName: Any? = null,
    val district: Any? = null,
    val state: Any? = null,
    val pincode: String? = null,
    val image: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val sarpanchName: Any? = null,
    val sarpanchPhone: String? = null,
    @get:PropertyName("active")
    val active: Boolean? = null,
    @get:PropertyName("isActive")
    val isVillageActive: Boolean? = null
) {
    val isCurrentlyActive: Boolean
        get() = active == true || isVillageActive == true
}

