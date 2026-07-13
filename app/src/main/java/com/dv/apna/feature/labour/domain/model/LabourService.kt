package com.dv.apna.feature.labour.domain.model

import androidx.annotation.DrawableRes

import com.dv.apna.core.common.UiText

data class LabourService(
    val title: UiText,
    @DrawableRes val icon: Int,
    val categoryId: String
)
