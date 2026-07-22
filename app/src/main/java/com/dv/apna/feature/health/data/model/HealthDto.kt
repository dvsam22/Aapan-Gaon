package com.dv.apna.feature.health.data.model

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties

@Keep
@IgnoreExtraProperties
data class HealthDto(
    val id: String = "",
    val address: Any? = null,
    val availability: Any? = null,
    val categoryId: String = "",
    val contact: String = "",
    val facilities: Any? = null,
    val image: String = "",
    val name: Any? = null,
    val services: Any? = null,
    val specialisation: Any? = null,
    val type: Any? = null,
    val villageId: String = ""
)
