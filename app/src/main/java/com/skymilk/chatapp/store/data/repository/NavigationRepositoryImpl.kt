package com.skymilk.chatapp.store.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.skymilk.chatapp.store.domain.model.NavigationState
import com.skymilk.chatapp.store.domain.repository.NavigationRepository
import com.skymilk.chatapp.store.data.utils.Constants.PreferencesKeys
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject


class NavigationRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : NavigationRepository {

    override suspend fun saveCurrentDestination(navigationState: NavigationState) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENT_DESTINATION_KEY] =
                Json.encodeToString(navigationState)
        }
    }

    override fun getCurrentDestination(): NavigationState {
        return runBlocking {
            dataStore.data.map { preferences ->
                val currentDestination = preferences[PreferencesKeys.CURRENT_DESTINATION_KEY]

                if (currentDestination.isNullOrEmpty())
                    NavigationState() // 기본값 반환
                else
                    Json.decodeFromString<NavigationState>(currentDestination) // 값이 있을 경우 디코드

            }.first()
        }
    }
}


