package com.dv.apna.feature.mandi.data.model

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties

@Keep
@IgnoreExtraProperties
data class MandiDto(
    val id: String = "",
    val address: Any? = null,
    val buyerName: Any? = null,
    val categoryId: String = "",
    val contact: String = "",
    val cropName: Any? = null,
    val date: Long = 0,
    val price: Double = 0.0,
    val trend: Any? = null,
    val unit: Any? = null,
    val villageId: String = ""
)
