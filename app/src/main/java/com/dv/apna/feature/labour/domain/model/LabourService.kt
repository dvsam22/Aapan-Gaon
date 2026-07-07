package com.dv.apna.feature.labour.domain.model

import androidx.annotation.DrawableRes

data class LabourService(
    val title: String,
    @DrawableRes val icon: Int,
    val categoryId: String
)
