package com.example.reddit.di

import com.example.reddit.data.MainRepository
import com.example.reddit.data.MainRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class SubredditRepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun provideSubredditRepository(impl: MainRepositoryImpl): MainRepository
}