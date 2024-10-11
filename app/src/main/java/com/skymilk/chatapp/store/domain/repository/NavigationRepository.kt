package com.skymilk.chatapp.store.domain.repository

import com.skymilk.chatapp.store.domain.model.NavigationState

interface NavigationRepository {
    suspend fun saveCurrentDestination(navigationState: NavigationState)

    fun getCurrentDestination(): NavigationState
}