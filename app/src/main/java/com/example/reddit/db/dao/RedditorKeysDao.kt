package com.example.reddit.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reddit.db.RemoteRedditorKeys

@Dao
interface RedditorKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(remoteKey: List<RemoteRedditorKeys>)

    @Query("SELECT * FROM remote_redditor_keys WHERE redditorCategory = :redditorCategory")
    fun getRedditorRemoteKeys(redditorCategory: String): RemoteRedditorKeys?

    @Query("DELETE FROM remote_redditor_keys WHERE redditorCategory = :redditorCategory")
    fun clearRemoteKeys(redditorCategory: String)
}