package com.example.reddit.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_redditor_keys")
data class RemoteRedditorKeys (
    @PrimaryKey
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val redditorCategory: String,
    val nextKey : String?
)