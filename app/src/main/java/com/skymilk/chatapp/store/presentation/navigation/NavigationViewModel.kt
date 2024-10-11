package com.skymilk.chatapp.store.presentation.navigation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavArgument
import com.skymilk.chatapp.store.domain.model.NavigationState
import com.skymilk.chatapp.store.domain.usecase.navigation.NavigationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val navigationUseCases: NavigationUseCases
) : ViewModel() {

    private val _navigationState = MutableStateFlow(NavigationState())
    val navigationState = _navigationState.asStateFlow()

    fun updateCurrentDestination(
        destination: String,
        map: Map<String, String>?
    ) {
        viewModelScope.launch {
            _navigationState.value = NavigationState(
                destination,
                map ?: emptyMap()
            )

            navigationUseCases.saveCurrentDestination(
                _navigationState.value
            )
        }
    }
}