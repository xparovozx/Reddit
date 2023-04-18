package com.example.reddit.di

import com.example.reddit.data.UserRepository
import com.example.reddit.data.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class UserRepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun provideUserRepository(impl: UserRepositoryImpl): UserRepository
}