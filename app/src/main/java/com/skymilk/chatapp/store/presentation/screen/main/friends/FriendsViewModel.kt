package com.skymilk.chatapp.store.presentation.screen.main.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.usecase.user.UserUseCases
import com.skymilk.chatapp.store.presentation.utils.Event
import com.skymilk.chatapp.store.presentation.utils.sendEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class FriendsViewModel @AssistedInject constructor(
    @Assisted private val userId: String,
    private val userUseCases: UserUseCases
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(userId: String): FriendsViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            userId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(userId) as T
            }
        }
    }

    private val _friendsState = MutableStateFlow<FriendsState>(FriendsState.Initial)
    val friendsState = _friendsState.asStateFlow()

    init {
        loadFriends()
    }

    fun loadFriends() {
        viewModelScope.launch {
            userUseCases.getFriends(userId)
                .onStart {
                    _friendsState.value = FriendsState.Loading
                }.catch { exception ->
//                    val message = exception.message ?: "친구 목록을 불러오지 못하였습니다."
                    val message = "친구 목록을 불러오지 못하였습니다."

                    sendEvent(Event.Toast(message))
                    _friendsState.value = FriendsState.Error(message)
                }.collectLatest { friends ->
                    _friendsState.value = FriendsState.Success(friends)
                }
        }
    }

}