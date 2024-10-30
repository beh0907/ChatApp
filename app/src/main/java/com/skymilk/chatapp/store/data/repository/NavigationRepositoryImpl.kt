package com.skymilk.chatapp.store.data.repository

import com.skymilk.chatapp.store.domain.model.NavigationState
import com.skymilk.chatapp.store.domain.repository.NavigationRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NavigationRepositoryImpl @Inject constructor() : NavigationRepository {
    private var currentDestination: NavigationState = NavigationState()

    override suspend fun saveCurrentDestination(navigationState: NavigationState) {
        currentDestination = navigationState
    }

    override fun getCurrentDestination(): NavigationState {
        return currentDestination
    }
}



