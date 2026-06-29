package com.dv.apna.feature.construction.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.feature.construction.domain.model.MaterialShopModel
import com.dv.apna.feature.construction.domain.model.MaterialTypePrice
import com.dv.apna.feature.construction.presentation.effect.MaterialShopsEffect
import com.dv.apna.feature.construction.presentation.event.MaterialShopsEvent
import com.dv.apna.feature.construction.presentation.state.MaterialShopsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MaterialShopsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(MaterialShopsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MaterialShopsEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadShops()
    }

    private fun loadShops() {
        val dummyShops = listOf(
            MaterialShopModel(
                id = "1",
                name = "Shree Balaji Bricks",
                address = "Rampur Village (Near Middle School)",
                materials = listOf(
                    MaterialTypePrice("Cement", "₹430 / 50Kg"),
                    MaterialTypePrice("Concrete", "₹80 / Kg"),
                    MaterialTypePrice("Sand", "₹60 / sqft"),
                    MaterialTypePrice("Iron Rods", "₹100 / Kg")
                )
            ),
            MaterialShopModel(
                id = "2",
                name = "Mahadev Bricks & Co.",
                address = "Rampur Village (Near Middle School)",
                materials = listOf(
                    MaterialTypePrice("Cement", "₹430 / 50Kg"),
                    MaterialTypePrice("Concrete", "₹80 / Kg"),
                    MaterialTypePrice("Sand", "₹60 / sqft"),
                    MaterialTypePrice("Iron Rods", "₹100 / Kg")
                )
            ),
            MaterialShopModel(
                id = "3",
                name = "Sri Lakshmi Brick Works",
                address = "Rampur Village (Near Middle School)",
                materials = listOf(
                    MaterialTypePrice("Cement", "₹430 / 50Kg"),
                    MaterialTypePrice("Concrete", "₹80 / Kg"),
                    MaterialTypePrice("Sand", "₹60 / sqft"),
                    MaterialTypePrice("Iron Rods", "₹100 / Kg")
                )
            )
        )
        _state.update { it.copy(shops = dummyShops) }
    }

    fun onEvent(event: MaterialShopsEvent) {
        when (event) {
            is MaterialShopsEvent.BackClick -> {
                viewModelScope.launch { _effect.emit(MaterialShopsEffect.NavigateBack) }
            }
            is MaterialShopsEvent.CallClick -> {
                viewModelScope.launch { _effect.emit(MaterialShopsEffect.DialPhone(event.phone)) }
            }
        }
    }
}
