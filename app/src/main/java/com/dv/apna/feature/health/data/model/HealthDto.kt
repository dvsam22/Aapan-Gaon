package com.dv.apna.feature.health.data.model

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties

@Keep
@IgnoreExtraProperties
data class HealthDto(
    val id: String = "",
    val address: String = "",
    val availability: String = "",
    val categoryId: String = "",
    val contact: String = "",
    val facilities: String = "",
    val image: String = "",
    val name: String = "",
    val services: String = "",
    val specialisation: String = "",
    val type: String = "",
    val villageId: String = ""
)
