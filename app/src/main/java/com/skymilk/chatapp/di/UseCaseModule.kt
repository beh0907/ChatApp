package com.skymilk.chatapp.di

import com.skymilk.chatapp.store.domain.repository.AuthRepository
import com.skymilk.chatapp.store.domain.usecase.auth.AuthUseCases
import com.skymilk.chatapp.store.domain.usecase.auth.GetCurrentUser
import com.skymilk.chatapp.store.domain.usecase.auth.SignInWithEmailAndPassword
import com.skymilk.chatapp.store.domain.usecase.auth.SignInWithGoogle
import com.skymilk.chatapp.store.domain.usecase.auth.SignOut
import com.skymilk.chatapp.store.domain.usecase.auth.SignUpWithEmailAndPassword
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
    fun provideSignInWithGoogleUseCase(authRepository: AuthRepository): AuthUseCases =
        AuthUseCases(
            SignInWithGoogle(authRepository),
            SignInWithEmailAndPassword(authRepository),
            SignUpWithEmailAndPassword(authRepository),
            SignOut(authRepository),
            GetCurrentUser(authRepository)
        )
}