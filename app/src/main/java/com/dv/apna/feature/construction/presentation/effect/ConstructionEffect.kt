package com.dv.apna.feature.construction.presentation.effect

sealed interface ConstructionEffect {
    data object NavigateBack : ConstructionEffect
    data object NavigateToBricks : ConstructionEffect
    data object NavigateToMaterialShops : ConstructionEffect
    data object NavigateToHardwareShops : ConstructionEffect
}
