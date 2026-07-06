package com.dv.apna.feature.mandi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.feature.mandi.domain.model.CropPriceModel
import com.dv.apna.feature.mandi.domain.model.LocalBuyerModel
import com.dv.apna.feature.mandi.domain.model.MarketPriceModel
import com.dv.apna.feature.mandi.presentation.effect.MandiEffect
import com.dv.apna.feature.mandi.presentation.event.MandiEvent
import com.dv.apna.feature.mandi.presentation.state.MandiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MandiViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(MandiState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MandiEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadCropPrices()
        loadMarketPrices()
        loadLocalBuyers()
    }

    private fun loadCropPrices() {
        val dummyPrices = listOf(
            CropPriceModel("1", "Wheat", "1 Quintal", "₹2,450"),
            CropPriceModel("2", "Paddy (Rice)", "1 Quintal", "₹2,320"),
            CropPriceModel("3", "Maize", "1 Quintal", "₹2,180"),
            CropPriceModel("4", "Cotton", "1 Quintal", "₹7,250"),
            CropPriceModel("5", "Soybean", "1 Quintal", "₹4,980"),
            CropPriceModel("6", "Mustard", "1 Quintal", "₹6,350"),
            CropPriceModel("7", "Groundnut", "1 Quintal", "₹6,850"),
            CropPriceModel("8", "Sugarcane", "1 Quintal", "₹380"),
            CropPriceModel("9", "Chickpeas", "1 Quintal", "₹5,950"),
            CropPriceModel("10", "Pigeon Pea (Tur)", "1 Quintal", "₹7,450"),
            CropPriceModel("11", "Black Gram (Urad)", "1 Quintal", "₹7,150"),
            CropPriceModel("12", "Green Gram (Moong)", "1 Quintal", "₹7,850"),
            CropPriceModel("13", "Barley", "1 Quintal", "₹2,150"),
            CropPriceModel("14", "Bajra (Pearl Millet)", "1 Quintal", "₹2,420")
        )
        _state.update { it.copy(cropPrices = dummyPrices) }
    }

    private fun loadMarketPrices() {
        val dummyPrices = listOf(
            MarketPriceModel("1", "Tomato", "1 Kg", "₹35"),
            MarketPriceModel("2", "Potato", "1 Kg", "₹28"),
            MarketPriceModel("3", "Onion", "1 Kg", "₹32"),
            MarketPriceModel("4", "Brinjal (Eggplant)", "1 Kg", "₹45"),
            MarketPriceModel("5", "Cabbage", "1 Kg", "₹30"),
            MarketPriceModel("6", "Cauliflower", "1 Kg", "₹40"),
            MarketPriceModel("7", "Carrot", "1 Kg", "₹55"),
            MarketPriceModel("8", "Radish", "1 Kg", "₹30"),
            MarketPriceModel("9", "Lady Finger (Okra)", "1 Kg", "₹60"),
            MarketPriceModel("10", "Green Chilli", "1 Kg", "₹90"),
            MarketPriceModel("11", "Capsicum", "1 Kg", "₹80"),
            MarketPriceModel("12", "Cucumber", "1 Kg", "₹40"),
            MarketPriceModel("13", "Bitter Gourd", "1 Kg", "₹65"),
            MarketPriceModel("14", "Spinach", "1 Bunch", "₹25"),
            MarketPriceModel("15", "Coriander", "1 Bunch", "₹20"),
            MarketPriceModel("16", "Green Peas", "1 Kg", "₹95"),
            MarketPriceModel("17", "Pumpkin", "1 Kg", "₹35"),
            MarketPriceModel("18", "Beetroot", "1 Kg", "₹50")
        )
        _state.update { it.copy(marketPrices = dummyPrices) }
    }

    private fun loadLocalBuyers() {
        val dummyBuyers = listOf(
            LocalBuyerModel("1", "Ramchand", "Rampur Village (Near Middle School)", "Vegetables", "1234567890"),
            LocalBuyerModel("2", "Premchand", "Rampur Village (Near Middle School)", "Fruits", "1234567890"),
            LocalBuyerModel("3", "Sundar", "Rampur Village (Near Middle School)", "Rice", "1234567890"),
            LocalBuyerModel("4", "Rahul", "Rampur Village (Near Middle School)", "Vegetables", "1234567890")
        )
        _state.update { it.copy(localBuyers = dummyBuyers) }
    }

    fun onEvent(event: MandiEvent) {
        when (event) {
            is MandiEvent.BackClick -> {
                viewModelScope.launch {
                    _effect.emit(MandiEffect.NavigateBack)
                }
            }
            is MandiEvent.CropPricesClick -> {
                viewModelScope.launch {
                    _effect.emit(MandiEffect.NavigateToCropPrices)
                }
            }
            is MandiEvent.TodayMarketClick -> {
                viewModelScope.launch {
                    _effect.emit(MandiEffect.NavigateToTodayMarket)
                }
            }
            is MandiEvent.LocalBuyersClick -> {
                viewModelScope.launch {
                    _effect.emit(MandiEffect.NavigateToLocalBuyers)
                }
            }
        }
    }
}
