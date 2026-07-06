package com.dv.apna.feature.news.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.feature.news.domain.model.NewsModel
import com.dv.apna.feature.news.domain.model.NoticeModel
import com.dv.apna.feature.news.presentation.effect.NewsEffect
import com.dv.apna.feature.news.presentation.event.NewsEvent
import com.dv.apna.feature.news.presentation.state.NewsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(NewsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<NewsEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val dummyNews = listOf(
            NewsModel(
                "1",
                "Good News for farmers: Government increases MSP for Kharif crops",
                "The government has announced an increase in the Minimum Support Price (MSP) for.....",
                "The Central Government has announced an increase in the Minimum Support Price (MSP) for major Kharif crops for the upcoming sowing season. The revised MSP aims to provide better returns to farmers while encouraging higher agricultural production across the country.\n\nUnder the new rates, crops such as Paddy, Maize, Jowar, Bajra, Ragi, Tur (Arhar), Moong, Urad, Groundnut, Soybean, Sunflower, Sesamum, Cotton, and Nigerseed will receive higher procurement prices. The increase varies by crop, with some crops receiving a significantly higher MSP to promote cultivation and improve farmers' incomes.\n\nOfficials stated that the revised MSP has been calculated to ensure farmers receive a fair margin over the cost of cultivation. The government has also assured that procurement agencies will be prepared to purchase eligible crops at the announced MSP during the harvest season.\n\nFarmers are encouraged to contact their nearest cooperative societies or procurement centers for more detailed information on the revised prices and procurement process.",
                "2 hours ago",
                ""
            ),
            NewsModel(
                "2",
                "Rain alert in 5 villages of Rampur block",
                "Light to moderate rainfall is expected tomorrow across Rampur Block. Farmers are advised to complete harvesting activities....",
                "",
                "4 Hours",
                ""
            ),
            NewsModel(
                "3",
                "Wheat Prices Rise in Local Market",
                "Wheat prices have increased by ₹150 per quintal in the local mandi due to higher demand and limited arrivals.",
                "",
                "6 Hours",
                ""
            )
        )

        val dummyNotices = listOf(
            NoticeModel(
                "1",
                "PM-KISAN 17th Installment",
                "The 17th installment of the PM-KISAN scheme will be credited to eligible farmers' bank accounts....",
                "The Government of India has announced the release of the 17th installment of the PM-KISAN (Pradhan Mantri Kisan Samman Nidhi) scheme. Under this installment, eligible farmers will receive ₹2,000 directly in their linked bank accounts.\n\nFarmers are advised to ensure that their e-KYC is completed and their bank accounts are linked with Aadhaar to avoid any delays in receiving the payment. You can check your status on the official PM-KISAN portal or visit your nearest Common Service Center (CSC) for assistance.",
                "12 June 2025"
            ),
            NoticeModel(
                "2",
                "Water Conservation Campaign",
                "Join the village water conservation campaign.",
                "The local administration is launching a month-long Water Conservation Campaign to promote efficient water usage and rainwater harvesting in our block. The campaign will include workshops on building low-cost harvesting units and sessions on sustainable farming practices.\n\nWe encourage all villagers to participate in the launch event scheduled at the Panchayat Bhawan. Together, we can ensure a water-secure future for our village.",
                "15 June 2025"
            )
        )

        _state.update { 
            it.copy(
                breakingNews = dummyNews,
                notices = dummyNotices
            ) 
        }
    }

    fun onEvent(event: NewsEvent) {
        when (event) {
            NewsEvent.BackClick -> {
                viewModelScope.launch { _effect.emit(NewsEffect.NavigateBack) }
            }
            is NewsEvent.NewsClick -> {
                viewModelScope.launch { _effect.emit(NewsEffect.NavigateToNewsDetails(event.id)) }
            }
            is NewsEvent.NoticeClick -> {
                viewModelScope.launch { _effect.emit(NewsEffect.NavigateToNoticeDetails(event.id)) }
            }
        }
    }
}
