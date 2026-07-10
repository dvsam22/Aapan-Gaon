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
    @get:PropertyName("active")
    @set:PropertyName("active")
    var active: Boolean? = null
)
