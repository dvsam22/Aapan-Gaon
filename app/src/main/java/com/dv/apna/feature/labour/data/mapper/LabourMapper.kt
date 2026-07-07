package com.dv.apna.feature.labour.data.mapper

import com.dv.apna.feature.labour.data.model.LabourDto
import com.dv.apna.feature.labour.domain.model.LabourDetails

fun LabourDto.toDomain(): LabourDetails {
    return LabourDetails(
        id = id ?: "",
        name = name ?: "",
        categoryId = categoryId ?: "",
        charges = charges ?: "",
        contact = contact ?: "",
        experience = experience ?: "",
        image = image ?: "",
        location = location ?: "",
        skills = skills ?: "",
        villageId = villageId ?: ""
    )
}
