package com.dv.apna.feature.news.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.news.domain.usecase.GetNewsDataUseCase
import com.dv.apna.feature.news.presentation.effect.NewsEffect
import com.dv.apna.feature.news.presentation.event.NewsEvent
import com.dv.apna.feature.news.presentation.state.NewsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsDataUseCase: GetNewsDataUseCase,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(NewsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<NewsEffect>()
    val effect = _effect.asSharedFlow()

    init {
        fetchNews()
    }

    private fun fetchNews() {
        viewModelScope.launch {
            preferenceManager.villageId.flatMapLatest { villageId ->
                getNewsDataUseCase(villageId ?: "")
            }.collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        val allNews = result.data ?: emptyList()
                        _state.update {
                            it.copy(
                                isLoading = false,
                                news = allNews.filter { item -> item.category == "news" },
                                notices = allNews.filter { item -> item.category == "notice" }
                            )
                        }
                    }

                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }

                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }
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
