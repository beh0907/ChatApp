package com.skymilk.chatapp.store.domain.repository

import com.skymilk.chatapp.store.domain.model.NavigationState
import kotlinx.coroutines.flow.Flow

interface NavigationRepository {
    suspend fun saveCurrentDestination(navigationState: NavigationState)

    fun getCurrentDestination(): Flow<NavigationState>
}