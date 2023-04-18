package com.example.reddit.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.reddit.data.RedditItem
import com.example.reddit.db.DataBaseContract

@Dao
interface RedditorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRedditors(redditor : List<RedditItem.Redditor>)

    @Transaction
    @Query("SELECT * FROM ${DataBaseContract.RedditorContract.TABLE_NAME}")
    fun getAllRedditorsWithRedditorInfo() : PagingSource<Int, RedditItem.Redditor>

    @Query("DELETE FROM ${DataBaseContract.RedditorContract.TABLE_NAME}")
    fun cleanRedditors()
}