package com.dv.apna.feature.construction.presentation.event

sealed interface ConstructionEvent {
    data object BackClick : ConstructionEvent
    data object BricksClick : ConstructionEvent
    data object MaterialShopsClick : ConstructionEvent
    data object HardwareShopsClick : ConstructionEvent
    data object LinterMachineClick : ConstructionEvent
    data object ShutteringClick : ConstructionEvent
}
