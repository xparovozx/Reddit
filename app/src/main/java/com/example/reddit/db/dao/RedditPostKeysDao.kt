package com.example.reddit.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reddit.db.RemotePostKeys

@Dao
interface RedditPostKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(remoteKey: List<RemotePostKeys>)

    @Query("SELECT * FROM remote_post_keys WHERE postCategory = :postCategory")
    fun getRedditPostRemoteKeys(postCategory: String): RemotePostKeys?

    @Query("DELETE FROM remote_post_keys WHERE postCategory = :postCategory")
    fun clearRemoteKeys(postCategory: String)
}