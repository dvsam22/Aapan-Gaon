package com.dv.apna.feature.home.data.mapper

import com.dv.apna.feature.home.data.model.HomeDto
import com.dv.apna.feature.home.domain.model.HomeModel

fun HomeDto.toDomain(): HomeModel {
    return HomeModel(
        id = id ?: "",
        title = title ?: "",
        description = description ?: ""
    )
}
