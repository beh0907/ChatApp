package com.skymilk.chatapp.store.presentation.navigation

import androidx.lifecycle.ViewModel
import com.skymilk.chatapp.store.presentation.navigation.routes.Navigations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class NavigationViewModel : ViewModel() {

    private val _currentScreen = MutableStateFlow<Navigations?>(null)
    val currentScreen: StateFlow<Navigations?> = _currentScreen

    fun updateCurrentScreen(screen: Navigations) {
        _currentScreen.value = screen
    }
}