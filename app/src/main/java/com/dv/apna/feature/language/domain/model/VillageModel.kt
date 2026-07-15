package com.dv.apna.feature.language.domain.model

data class VillageModel(
    val id: String,
    val villageName: String,
    val district: String,
    val state: String,
    val pincode: String,
    val image: String,
    val lat: Double,
    val lng: Double,
    val sarpanchName: String,
    val sarpanchPhone: String
)
