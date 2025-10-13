package com.trendy.minishop.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trendy.domain.usecase.ObserveCartCountUseCase
import com.trendy.domain.usecase.ObserveFavoriteCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    observeCartCount: ObserveCartCountUseCase,
    observeFavoriteCount: ObserveFavoriteCountUseCase
) : ViewModel() {

    val cartItemCountState: StateFlow<Int> =
        observeCartCount()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Companion.WhileSubscribed(5_000),
                initialValue = 0
            )

    val favoriteCountState: StateFlow<Int> =
        observeFavoriteCount()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Companion.WhileSubscribed(5_000),
                initialValue = 0
            )
}