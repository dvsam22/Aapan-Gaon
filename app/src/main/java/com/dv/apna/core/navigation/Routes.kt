package com.dv.apna.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Splash : Route

    @Serializable
    data object Language : Route

    @Serializable
    data object ChangeLanguage : Route

    @Serializable
    data object ChangeVillage : Route

    @Serializable
    data object MainGraph : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object Services : Route

    @Serializable
    data object Health : Route

    @Serializable
    data object Doctors : Route

    @Serializable
    data object Hospitals : Route

    @Serializable
    data object Pharmacy : Route

    @Serializable
    data object Ambulance : Route

    @Serializable
    data object Police : Route

    @Serializable
    data object Mandi : Route

    @Serializable
    data object CropPrices : Route

    @Serializable
    data object TodayMarket : Route

    @Serializable
    data object LocalBuyers : Route

    @Serializable
    data object Construction : Route

    @Serializable
    data object BricksSuppliers : Route

    @Serializable
    data object MaterialShops : Route

    @Serializable
    data object HardwareShops : Route

    @Serializable
    data object LabourBoard : Route

    @Serializable
    data class LabourDetails(val category: String) : Route

    @Serializable
    data object Transport : Route

    @Serializable
    data class TransportDetails(val categoryId: String, val categoryName: String) : Route

    @Serializable
    data object News : Route

    @Serializable
    data class NewsDetails(val id: String) : Route

    @Serializable
    data class NoticeDetails(val id: String) : Route

    @Serializable
    data object Notifications : Route

    @Serializable
    data class NotificationDetails(val id: String) : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object AboutUs : Route

    @Serializable
    data object PrivacyPolicy : Route

    @Serializable
    data object TermsAndConditions : Route
}
