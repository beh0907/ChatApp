package com.skymilk.chatapp.store.presentation.screen.main.chatList

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn


class ChatListViewModel @AssistedInject constructor(
    @Assisted private val userId: String,
    private val chatRepository: ChatRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(userId: String): ChatListViewModel
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

    val chatRooms = chatRepository.getChatRooms(userId)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

}

