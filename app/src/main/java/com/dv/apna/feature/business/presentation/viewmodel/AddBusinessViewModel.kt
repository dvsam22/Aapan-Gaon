package com.dv.apna.feature.business.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.R
import com.dv.apna.core.common.Resource
import com.dv.apna.core.common.UiText
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.business.domain.model.BusinessRegistrationModel
import com.dv.apna.feature.business.domain.usecase.SubmitBusinessUseCase
import com.dv.apna.feature.business.presentation.effect.AddBusinessEffect
import com.dv.apna.feature.business.presentation.event.AddBusinessEvent
import com.dv.apna.feature.business.presentation.state.AddBusinessState
import com.dv.apna.feature.language.domain.model.VillageModel
import com.dv.apna.feature.language.domain.repository.LanguageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBusinessViewModel @Inject constructor(
    private val submitBusinessUseCase: SubmitBusinessUseCase,
    private val preferenceManager: PreferenceManager,
    private val languageRepository: LanguageRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddBusinessState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AddBusinessEffect>()
    val effect = _effect.asSharedFlow()

    private var allVillages: List<VillageModel> = emptyList()
    private var defaultVillageId: String? = null
    private var defaultVillageName: String? = null

    init {
        loadCurrentVillage()
    }

    private fun loadCurrentVillage() {
        viewModelScope.launch {
            defaultVillageId = preferenceManager.villageId.firstOrNull()
            defaultVillageName = preferenceManager.villageName.firstOrNull()
            _state.update {
                it.copy(
                    villageId = defaultVillageId,
                    villageName = defaultVillageName,
                    address = if (it.address.isBlank() && !defaultVillageName.isNullOrBlank()) defaultVillageName!! else it.address
                )
            }
            languageRepository.getVillages().collectLatest { result ->
                if (result is Resource.Success) {
                    allVillages = result.data ?: emptyList()
                }
            }
        }
    }

    fun onEvent(event: AddBusinessEvent) {
        when (event) {
            is AddBusinessEvent.ContactNameChanged -> {
                _state.update { it.copy(contactName = event.name, errorMessage = null) }
            }
            is AddBusinessEvent.BusinessNameChanged -> {
                _state.update { it.copy(businessName = event.businessName, errorMessage = null) }
            }
            is AddBusinessEvent.CategorySelected -> {
                _state.update { it.copy(selectedCategory = event.category, errorMessage = null) }
            }
            is AddBusinessEvent.PhoneNumberChanged -> {
                val filtered = event.phone.filter { it.isDigit() }.take(10)
                _state.update { it.copy(phoneNumber = filtered, errorMessage = null) }
            }
            is AddBusinessEvent.AddressChanged -> {
                _state.update { it.copy(address = event.address, errorMessage = null) }
            }
            is AddBusinessEvent.DescriptionChanged -> {
                _state.update { it.copy(description = event.description) }
            }
            AddBusinessEvent.ResetForm -> {
                _state.update {
                    AddBusinessState(
                        villageId = defaultVillageId,
                        villageName = defaultVillageName,
                        address = defaultVillageName ?: ""
                    )
                }
            }
            AddBusinessEvent.Submit -> {
                validateAndSubmit()
            }
        }
    }

    private fun validateAndSubmit() {
        val s = _state.value
        if (s.contactName.trim().isEmpty()) {
            _state.update { it.copy(errorMessage = UiText.StringResource(R.string.error_name_empty)) }
            return
        }
        if (s.businessName.trim().isEmpty()) {
            _state.update { it.copy(errorMessage = UiText.StringResource(R.string.error_business_name_empty)) }
            return
        }
        if (s.selectedCategory.trim().isEmpty()) {
            _state.update { it.copy(errorMessage = UiText.StringResource(R.string.error_category_empty)) }
            return
        }
        if (s.phoneNumber.trim().length != 10) {
            _state.update { it.copy(errorMessage = UiText.StringResource(R.string.error_mobile_invalid)) }
            return
        }
        if (s.address.trim().isEmpty()) {
            _state.update { it.copy(errorMessage = UiText.StringResource(R.string.error_address_empty)) }
            return
        }

        viewModelScope.launch {
            val enteredAddress = s.address.trim()
            val (resolvedVillageId, resolvedVillageName) = resolveVillage(enteredAddress)

            val model = BusinessRegistrationModel(
                contactName = s.contactName.trim(),
                businessName = s.businessName.trim(),
                category = s.selectedCategory.trim(),
                phoneNumber = s.phoneNumber.trim(),
                address = enteredAddress,
                description = s.description.trim(),
                villageId = resolvedVillageId,
                villageName = resolvedVillageName
            )

            submitBusinessUseCase(model).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isSubmittedSuccess = true,
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = UiText.DynamicString(result.message ?: "Failed to submit. Please try again.")
                            )
                        }
                    }
                }
            }
        }
    }

    private fun resolveVillage(enteredAddress: String): Pair<String?, String> {
        val trimmed = enteredAddress.trim()
        if (trimmed.isBlank()) {
            return Pair(defaultVillageId, defaultVillageName ?: "")
        }

        val defName = defaultVillageName
        // 1. If matches the user's default/selected village
        if (!defName.isNullOrBlank() && trimmed.equals(defName, ignoreCase = true)) {
            return Pair(defaultVillageId, defName)
        }

        // 2. If exactly matches any active village from the database
        val exactMatch = allVillages.find { v ->
            v.villageName.equals(trimmed, ignoreCase = true)
        }
        if (exactMatch != null) {
            return Pair(exactMatch.id, exactMatch.villageName)
        }

        // 3. If entered text contains a known village name
        val partialMatch = allVillages.find { v ->
            trimmed.contains(v.villageName, ignoreCase = true)
        }
        if (partialMatch != null) {
            return Pair(partialMatch.id, trimmed)
        }

        // 4. Custom village entered by the user
        val slug = trimmed
            .lowercase()
            .replace("\\s+".toRegex(), "_")
            .filter { it.isLetterOrDigit() || it == '_' }
            .take(30)
        val customVillageId = if (slug.isNotBlank()) "v_$slug" else "v_custom"

        return Pair(customVillageId, trimmed)
    }
}
