package com.skymilk.chatapp.di

import com.skymilk.chatapp.store.domain.repository.AuthRepository
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import com.skymilk.chatapp.store.domain.repository.NavigationRepository
import com.skymilk.chatapp.store.domain.repository.ChatRoomSettingRepository
import com.skymilk.chatapp.store.domain.repository.StorageRepository
import com.skymilk.chatapp.store.domain.repository.UserRepository
import com.skymilk.chatapp.store.domain.repository.UserSettingRepository
import com.skymilk.chatapp.store.domain.usecase.auth.AuthUseCases
import com.skymilk.chatapp.store.domain.usecase.auth.GetCurrentUser
import com.skymilk.chatapp.store.domain.usecase.auth.SignInWithEmailAndPassword
import com.skymilk.chatapp.store.domain.usecase.auth.SignInWithGoogle
import com.skymilk.chatapp.store.domain.usecase.auth.SignInWithKakao
import com.skymilk.chatapp.store.domain.usecase.auth.SignOut
import com.skymilk.chatapp.store.domain.usecase.auth.SignUpWithEmailAndPassword
import com.skymilk.chatapp.store.domain.usecase.chat.AddParticipants
import com.skymilk.chatapp.store.domain.usecase.chat.ChatUseCases
import com.skymilk.chatapp.store.domain.usecase.chat.CreateChatRoom
import com.skymilk.chatapp.store.domain.usecase.chat.ExitChatRoom
import com.skymilk.chatapp.store.domain.usecase.chat.GetChatRoom
import com.skymilk.chatapp.store.domain.usecase.chat.GetChatRooms
import com.skymilk.chatapp.store.domain.usecase.chat.GetRealtimeMessages
import com.skymilk.chatapp.store.domain.usecase.chat.GetOrCreateChatRoom
import com.skymilk.chatapp.store.domain.usecase.chat.SendImageMessage
import com.skymilk.chatapp.store.domain.usecase.chat.SendMessage
import com.skymilk.chatapp.store.domain.usecase.chat.UpdateParticipantsStatus
import com.skymilk.chatapp.store.domain.usecase.navigation.GetCurrentDestination
import com.skymilk.chatapp.store.domain.usecase.navigation.NavigationUseCases
import com.skymilk.chatapp.store.domain.usecase.navigation.SaveCurrentDestination
import com.skymilk.chatapp.store.domain.usecase.chatRoomSetting.DeleteAlarmSetting
import com.skymilk.chatapp.store.domain.usecase.chatRoomSetting.GetAlarmSetting
import com.skymilk.chatapp.store.domain.usecase.chatRoomSetting.GetAlarmsSetting
import com.skymilk.chatapp.store.domain.usecase.userSetting.GetUserAlarmSetting
import com.skymilk.chatapp.store.domain.usecase.chatRoomSetting.SaveAlarmSetting
import com.skymilk.chatapp.store.domain.usecase.userSetting.ToggleUserAlarmSetting
import com.skymilk.chatapp.store.domain.usecase.chatRoomSetting.ChatRoomSettingUseCases
import com.skymilk.chatapp.store.domain.usecase.storage.SaveChatMessageImage
import com.skymilk.chatapp.store.domain.usecase.storage.SaveProfileImage
import com.skymilk.chatapp.store.domain.usecase.storage.StorageUseCases
import com.skymilk.chatapp.store.domain.usecase.user.GetFriends
import com.skymilk.chatapp.store.domain.usecase.user.GetIsFriend
import com.skymilk.chatapp.store.domain.usecase.user.GetUser
import com.skymilk.chatapp.store.domain.usecase.user.SearchUser
import com.skymilk.chatapp.store.domain.usecase.user.SetFriend
import com.skymilk.chatapp.store.domain.usecase.user.UpdateFcmToken
import com.skymilk.chatapp.store.domain.usecase.user.UpdateProfile
import com.skymilk.chatapp.store.domain.usecase.user.UserUseCases
import com.skymilk.chatapp.store.domain.usecase.userSetting.GetRefuseIgnoringOptimization
import com.skymilk.chatapp.store.domain.usecase.userSetting.GetUserDarkModeSetting
import com.skymilk.chatapp.store.domain.usecase.userSetting.SetRefuseIgnoringOptimization
import com.skymilk.chatapp.store.domain.usecase.userSetting.ToggleUserDarkModeSetting
import com.skymilk.chatapp.store.domain.usecase.userSetting.UserSettingUseCases
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
            SignInWithKakao(authRepository),
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
            GetRealtimeMessages(chatRepository),
            SendMessage(chatRepository),
            SendImageMessage(chatRepository),
            CreateChatRoom(chatRepository),
            ExitChatRoom(chatRepository),
            AddParticipants(chatRepository),
            UpdateParticipantsStatus(chatRepository),
        )

    @Provides
    @Singleton
    fun provideUserUseCase(userRepository: UserRepository): UserUseCases =
        UserUseCases(
            UpdateProfile(userRepository),
            UpdateFcmToken(userRepository),
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

    @Provides
    @Singleton
    fun provideChatRoomSettingUseCases(chatRoomSettingRepository: ChatRoomSettingRepository): ChatRoomSettingUseCases =
        ChatRoomSettingUseCases(
            GetAlarmSetting(chatRoomSettingRepository),
            GetAlarmsSetting(chatRoomSettingRepository),
            SaveAlarmSetting(chatRoomSettingRepository),
            DeleteAlarmSetting(chatRoomSettingRepository),
        )

    @Provides
    @Singleton
    fun provideUserSettingUseCases(userSettingRepository: UserSettingRepository): UserSettingUseCases =
        UserSettingUseCases(
            GetUserAlarmSetting(userSettingRepository),
            ToggleUserAlarmSetting(userSettingRepository),

            GetUserDarkModeSetting(userSettingRepository),
            ToggleUserDarkModeSetting(userSettingRepository),

            GetRefuseIgnoringOptimization(userSettingRepository),
            SetRefuseIgnoringOptimization(userSettingRepository),
        )

    @Provides
    @Singleton
    fun provideNavigationUseCases(navigationRepository: NavigationRepository): NavigationUseCases =
        NavigationUseCases(
            GetCurrentDestination(navigationRepository),
            SaveCurrentDestination(navigationRepository),
        )
}