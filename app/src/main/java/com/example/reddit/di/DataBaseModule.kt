package com.example.reddit.di

import android.content.Context
import androidx.room.Room
import com.example.reddit.db.RedditDataBase
import com.example.reddit.db.dao.RedditPostDao
import com.example.reddit.db.dao.RedditPostKeysDao
import com.example.reddit.db.dao.RedditorDao
import com.example.reddit.db.dao.RedditorKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataBaseModule {

    @Provides
    fun providesRedditorKeysDao(dataBase: RedditDataBase) : RedditorKeysDao {
        return dataBase.redditorRemoteKeyDao()
    }

    @Provides
    fun providesRedditPostKeysDao(dataBase: RedditDataBase) : RedditPostKeysDao {
        return dataBase.redditPostRemoteKeyDao()
    }

    @Provides
    fun providesRedditPostDao(dataBase: RedditDataBase) : RedditPostDao {
        return dataBase.redditPostDao()
    }

    @Provides
    fun providesRedditorDao(dataBase: RedditDataBase) : RedditorDao {
        return dataBase.redditorDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context) : RedditDataBase {
        return Room.databaseBuilder(
            appContext,
            RedditDataBase::class.java,
            RedditDataBase.DB_NAME
        ).build()
    }
}