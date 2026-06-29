package com.dv.apna.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Splash : Route

    @Serializable
    data object Language : Route

    @Serializable
    data object MainGraph : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object Services : Route

    @Serializable
    data object Health : Route

    @Serializable
    data object Mandi : Route

    @Serializable
    data object Construction : Route

    @Serializable
    data object BricksSuppliers : Route

    @Serializable
    data object MaterialShops : Route

    @Serializable
    data object LabourBoard : Route

    @Serializable
    data class LabourDetails(val category: String) : Route

    @Serializable
    data object Transport : Route

    @Serializable
    data class TransportDetails(val category: String) : Route

    @Serializable
    data object News : Route

    @Serializable
    data object Notifications : Route

    @Serializable
    data object Settings : Route
}