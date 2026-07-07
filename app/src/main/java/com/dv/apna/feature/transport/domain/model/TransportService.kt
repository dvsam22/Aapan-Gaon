package com.dv.apna.feature.transport.domain.model

import androidx.annotation.DrawableRes

data class TransportService(
    val title: String,
    @DrawableRes val icon: Int,
    val categoryId: String = ""
)
