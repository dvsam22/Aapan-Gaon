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
    val location: String = "",
    val name: String = "",
    val vehicleType: String = "",
    val villageId: String = ""
)
