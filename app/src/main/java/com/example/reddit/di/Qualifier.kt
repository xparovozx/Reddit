package com.example.reddit.di

import com.squareup.moshi.JsonQualifier
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LoggingInterceptorQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TokenInterceptorQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppContextQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SecretPrefsQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DispatcherIO

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DispatcherMain

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DispatcherDefault

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class CommentsWithReplies




