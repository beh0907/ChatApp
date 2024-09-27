package com.skymilk.chatapp.di

import com.skymilk.chatapp.store.domain.repository.AuthRepository
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import com.skymilk.chatapp.store.domain.repository.StorageRepository
import com.skymilk.chatapp.store.domain.repository.UserRepository
import com.skymilk.chatapp.store.domain.usecase.auth.AuthUseCases
import com.skymilk.chatapp.store.domain.usecase.auth.GetCurrentUser
import com.skymilk.chatapp.store.domain.usecase.auth.SignInWithEmailAndPassword
import com.skymilk.chatapp.store.domain.usecase.auth.SignInWithGoogle
import com.skymilk.chatapp.store.domain.usecase.auth.SignOut
import com.skymilk.chatapp.store.domain.usecase.auth.SignUpWithEmailAndPassword
import com.skymilk.chatapp.store.domain.usecase.chat.ChatUseCases
import com.skymilk.chatapp.store.domain.usecase.chat.CreateChatRoom
import com.skymilk.chatapp.store.domain.usecase.chat.GetChatRoom
import com.skymilk.chatapp.store.domain.usecase.chat.GetOrCreateChatRoom
import com.skymilk.chatapp.store.domain.usecase.chat.GetChatRooms
import com.skymilk.chatapp.store.domain.usecase.chat.GetMessages
import com.skymilk.chatapp.store.domain.usecase.chat.SendImageMessage
import com.skymilk.chatapp.store.domain.usecase.chat.SendMessage
import com.skymilk.chatapp.store.domain.usecase.storage.SaveChatMessageImage
import com.skymilk.chatapp.store.domain.usecase.storage.SaveProfileImage
import com.skymilk.chatapp.store.domain.usecase.storage.StorageUseCases
import com.skymilk.chatapp.store.domain.usecase.user.GetFriends
import com.skymilk.chatapp.store.domain.usecase.user.GetIsFriend
import com.skymilk.chatapp.store.domain.usecase.user.GetUser
import com.skymilk.chatapp.store.domain.usecase.user.SearchUser
import com.skymilk.chatapp.store.domain.usecase.user.SetFriend
import com.skymilk.chatapp.store.domain.usecase.user.UpdateProfile
import com.skymilk.chatapp.store.domain.usecase.user.UserUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideAuthUseCase(authRepository: AuthRepository): AuthUseCases =
        AuthUseCases(
            SignInWithGoogle(authRepository),
            SignInWithEmailAndPassword(authRepository),
            SignUpWithEmailAndPassword(authRepository),
            SignOut(authRepository),
            GetCurrentUser(authRepository)
        )

    @Provides
    @Singleton
    fun provideChatUseCase(chatRepository: ChatRepository): ChatUseCases =
        ChatUseCases(
            GetChatRoom(chatRepository),
            GetChatRooms(chatRepository),
            GetOrCreateChatRoom(chatRepository),
            GetMessages(chatRepository),
            SendMessage(chatRepository),
            SendImageMessage(chatRepository),
            CreateChatRoom(chatRepository)
        )

    @Provides
    @Singleton
    fun provideUserUseCase(userRepository: UserRepository): UserUseCases =
        UserUseCases(
            UpdateProfile(userRepository),
            GetUser(userRepository),
            GetFriends(userRepository),
            GetIsFriend(userRepository),
            SetFriend(userRepository),
            SearchUser(userRepository)
        )

    @Provides
    @Singleton
    fun provideStorageUseCase(storageRepository: StorageRepository): StorageUseCases =
        StorageUseCases(
            SaveProfileImage(storageRepository),
            SaveChatMessageImage(storageRepository)
        )
}