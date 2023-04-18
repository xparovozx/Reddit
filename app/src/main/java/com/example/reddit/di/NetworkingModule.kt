package com.example.reddit.di

import android.app.Application
import android.content.Context
import com.example.reddit.data.CommentJsonAdapter
import com.example.reddit.networking.RedditApi
import com.example.reddit.oauth_data.AuthTokenInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkingModule {

    @Provides
    @LoggingInterceptorQualifier
    fun providesLoggingInterceptor(): Interceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)

    @Provides
    @TokenInterceptorQualifier
    fun providesTokenInterceptor(
        authTokenPreference: AuthTokenPreference
    ): Interceptor = AuthTokenInterceptor( authTokenPreference )

    @Provides
    @Singleton
    fun provideMyPreference(  @ApplicationContext appContext : Context) : AuthTokenPreference{
        return AuthTokenPreference(appContext)
    }

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun providesOkhttpClient(
        @LoggingInterceptorQualifier loggingInterceptor: Interceptor,
        @TokenInterceptorQualifier tokenInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(loggingInterceptor)
            .addNetworkInterceptor(tokenInterceptor)
            .followRedirects(true)
            .build()
    }

    @Provides
    @Singleton
    fun providesAdapter() : CommentJsonAdapter = CommentJsonAdapter()

    @Provides
    @Singleton
    fun providesMoshi(adapter: CommentJsonAdapter): Moshi = Moshi.Builder().add(adapter).build()

    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient,
                         moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://oauth.reddit.com")
            .addConverterFactory(
                MoshiConverterFactory.create(moshi)
            )
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providesApi(retrofit: Retrofit): RedditApi {
        return retrofit.create()
    }
}