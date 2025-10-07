package com.trendy.minishop.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trendy.domain.usecase.ObserveCartCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    observeCartCount: ObserveCartCountUseCase
) : ViewModel() {

    val cartItemCountState: StateFlow<Int> =
        observeCartCount()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0
            )
}
