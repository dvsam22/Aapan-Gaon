package com.dv.apna.feature.language.data.model

import com.google.firebase.firestore.PropertyName

data class VillageDto(
    val id: String? = null,
    val villageName: String? = null,
    val district: String? = null,
    val state: String? = null,
    val pincode: String? = null,
    val image: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val sarpanchName: String? = null,
    @get:PropertyName("active")
    @set:PropertyName("active")
    var active: Boolean? = null
)
