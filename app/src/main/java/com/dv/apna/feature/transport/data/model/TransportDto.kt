package com.dv.apna.feature.transport.data.model

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties

@Keep
@IgnoreExtraProperties
data class TransportDto(
    val id: String = "",
    val categoryId: String = "",
    val contact: String = "",
    val image: String = "",
    val location: Any? = null,
    val name: Any? = null,
    val vehicleType: Any? = null,
    val villageId: String = ""
)
