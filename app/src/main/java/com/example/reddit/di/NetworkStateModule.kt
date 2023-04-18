package com.example.reddit.di

import android.content.Context
import com.example.reddit.networking.NetworkState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkStateModule {

    @Provides
    @Singleton
    fun providesNetworkState(@ApplicationContext context: Context) : NetworkState {
        return NetworkState(context)
    }
}