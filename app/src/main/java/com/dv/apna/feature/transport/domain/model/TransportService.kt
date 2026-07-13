package com.dv.apna.feature.transport.domain.model

import androidx.annotation.DrawableRes

import com.dv.apna.core.common.UiText

data class TransportService(
    val title: UiText,
    @DrawableRes val icon: Int,
    val categoryId: String = ""
)
