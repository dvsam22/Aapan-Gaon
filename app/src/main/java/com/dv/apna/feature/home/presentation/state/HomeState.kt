package com.dv.apna.feature.home.presentation.state

import com.dv.apna.core.common.UiText
import com.dv.apna.feature.home.domain.model.BannerModel
import com.dv.apna.feature.home.domain.model.HomeModel

data class HomeState(
    val isLoading: Boolean = false,
    val banners: List<BannerModel> = emptyList(),
    val data: List<HomeModel> = emptyList(),
    val selectedVillage: String? = null,
    val error: UiText? = null
)
