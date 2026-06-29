package com.dv.apna.feature.labour.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dv.apna.R
import com.dv.apna.core.navigation.Route
import com.dv.apna.feature.labour.domain.model.LabourDetails
import com.dv.apna.feature.labour.domain.model.LabourService
import com.dv.apna.feature.labour.presentation.effect.LabourEffect
import com.dv.apna.feature.labour.presentation.event.LabourEvent
import com.dv.apna.feature.labour.presentation.state.LabourState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LabourViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(LabourState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LabourEffect>()
    val effect = _effect.asSharedFlow()

    init {
        getLabourServices()
        checkLabourDetails()
    }

    private fun checkLabourDetails() {
        try {
            val route = savedStateHandle.toRoute<Route.LabourDetails>()
            _state.update { it.copy(selectedCategory = route.category) }
            getLabourDetails(route.category)
        } catch (e: Exception) {
            // Not in LabourDetails route or no args
        }
    }

    private fun getLabourServices() {
        val services = listOf(
            LabourService("Rajmistri", R.drawable.rajmistri),
            LabourService("Plumber", R.drawable.plumber),
            LabourService("Electrician", R.drawable.electrician),
            LabourService("Carpenter", R.drawable.carpenter),
            LabourService("Tailor", R.drawable.tailor),
            LabourService("Labour", R.drawable.labour)
        )
        _state.update { it.copy(services = services) }
    }

    fun onEvent(event: LabourEvent) {
        when (event) {
            is LabourEvent.BackClick -> {
                viewModelScope.launch { _effect.emit(LabourEffect.NavigateBack) }
            }

            is LabourEvent.CategoryClick -> {
                _state.update { it.copy(selectedCategory = event.category) }
                getLabourDetails(event.category)
                viewModelScope.launch { _effect.emit(LabourEffect.NavigateToCategory(event.category)) }
            }
        }
    }

    private fun getLabourDetails(category: String) {
        // Mocking data for the selected category
        val details = listOf(
            LabourDetails(
                name = "Suresh Paswan",
                address = "Rampur Village (Near Middle School)",
                skills = "Pipe Fitting, Bathroom Fitting, Tap Repair",
                charges = "₹450 / Day",
                phoneNumber = "1234567890"
            ),
            LabourDetails(
                name = "Ramesh Yadav",
                address = "Rampur Village (Near Middle School)",
                skills = "Pipe Fitting, Bathroom Fitting, Tap Repair",
                charges = "₹450 / Day",
                phoneNumber = "1234567890"
            ),
            LabourDetails(
                name = "Vikram Kumar",
                address = "Rampur Village (Near Middle School)",
                skills = "Pipe Fitting, Bathroom Fitting, Tap Repair",
                charges = "₹450 / Day",
                phoneNumber = "1234567890"
            )
        )
        _state.update { it.copy(labourDetails = details) }
    }
}
