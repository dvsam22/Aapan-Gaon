package com.example.aapangav.feature.home.data.mapper

import com.example.aapangav.feature.home.data.model.HomeDto
import com.example.aapangav.feature.home.domain.model.HomeModel

fun HomeDto.toDomain(): HomeModel {
    return HomeModel(
        id = id ?: "",
        title = title ?: "",
        description = description ?: ""
    )
}
