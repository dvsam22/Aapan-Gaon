package com.dv.apna.feature.language.data.mapper

import com.dv.apna.feature.language.data.model.VillageDto
import com.dv.apna.feature.language.domain.model.VillageModel

fun VillageDto.toDomain(): VillageModel {
    return VillageModel(
        id = id ?: "",
        villageName = villageName ?: "",
        district = district ?: "",
        state = state ?: "",
        pincode = pincode ?: "",
        image = image ?: "",
        lat = lat ?: 0.0,
        lng = lng ?: 0.0,
        sarpanchName = sarpanchName ?: ""
    )
}
