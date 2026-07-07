package com.dv.apna.feature.mandi.data.model

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties

@Keep
@IgnoreExtraProperties
data class MandiDto(
    val id: String = "",
    val address: String = "",
    val buyerName: String = "",
    val categoryId: String = "",
    val contact: String = "",
    val cropName: String = "",
    val date: Long = 0,
    val price: Double = 0.0,
    val trend: String = "",
    val unit: String = "",
    val villageId: String = ""
)
