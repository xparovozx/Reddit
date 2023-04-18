package com.example.reddit.db

import android.content.Context
import androidx.room.Room

object Database {
    lateinit var instance: RedditDataBase
        private set

    fun init(context: Context) {
        instance = Room.databaseBuilder(
            context,
            RedditDataBase::class.java,
            RedditDataBase.DB_NAME)
            .build()
    }
}