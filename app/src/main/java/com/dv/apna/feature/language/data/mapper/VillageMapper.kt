package com.dv.apna.feature.language.data.mapper

import com.dv.apna.core.utils.toLocalizedSafeString
import com.dv.apna.feature.language.data.model.VillageDto
import com.dv.apna.feature.language.domain.model.VillageModel

fun VillageDto.toDomain(languageCode: String): VillageModel {
    return VillageModel(
        id = id ?: "",
        villageName = villageName.toLocalizedSafeString(languageCode),
        district = district.toLocalizedSafeString(languageCode),
        state = state.toLocalizedSafeString(languageCode),
        pincode = pincode ?: "",
        image = image ?: "",
        lat = lat ?: 0.0,
        lng = lng ?: 0.0,
        sarpanchName = sarpanchName.toLocalizedSafeString(languageCode),
        sarpanchPhone = sarpanchPhone ?: ""
    )
}
