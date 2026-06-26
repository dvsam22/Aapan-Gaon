package com.dv.apna.feature.news.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.dv.apna.feature.news.presentation.state.NewsState
import com.dv.apna.feature.news.presentation.event.NewsEvent
import com.dv.apna.feature.news.presentation.effect.NewsEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(NewsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<NewsEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: NewsEvent) {
        // Handle events
    }
}