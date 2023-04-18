package com.example.reddit.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_post_keys")
data class RemotePostKeys (
    @PrimaryKey
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val postCategory: String,
    val nextKey : String?
)