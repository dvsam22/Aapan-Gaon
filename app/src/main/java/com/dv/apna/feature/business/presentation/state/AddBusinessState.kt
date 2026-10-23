package com.dv.apna.feature.business.presentation.state

import com.dv.apna.core.common.UiText

data class AddBusinessState(
    val contactName: String = "",
    val businessName: String = "",
    val selectedCategory: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val description: String = "",
    val villageId: String? = null,
    val villageName: String? = null,
    val isLoading: Boolean = false,
    val isSubmittedSuccess: Boolean = false,
    val errorMessage: UiText? = null
)
