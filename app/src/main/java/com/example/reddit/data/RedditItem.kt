package com.example.reddit.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.reddit.db.DataBaseContract
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

sealed class RedditItem {

    @Parcelize
    @JsonClass(generateAdapter = true)
    @Entity(tableName = DataBaseContract.RedditorContract.TABLE_NAME)
    data class Redditor(
        @PrimaryKey
        @ColumnInfo(name = DataBaseContract.RedditorContract.Columns.NAME)
        val name: String,

        @Json(name = "is_friend")
        @ColumnInfo(name = DataBaseContract.RedditorContract.Columns.IS_FRIEND)
        val isFriend: Boolean,

        @Embedded
        val subreddit: RedditorInfo,
        ) : Parcelable, RedditItem()

    @Parcelize
    @JsonClass(generateAdapter = true)
    @Entity(tableName = DataBaseContract.RedditPostContract.TABLE_NAME)
    data class RedditPost(
        @PrimaryKey
        @ColumnInfo(name = DataBaseContract.RedditPostContract.Columns.NAME)
        val name: String,

        @Json(name = "subreddit_id")
        @ColumnInfo(name = DataBaseContract.RedditPostContract.Columns.SUBREDDIT_ID)
        val subredditId: String? = "",

        @ColumnInfo(name = DataBaseContract.RedditPostContract.Columns.AUTHOR)
        val author: String? = "",

        @ColumnInfo(name = DataBaseContract.RedditPostContract.Columns.TITLE)
        val title: String? = "",

        @ColumnInfo(name = DataBaseContract.RedditPostContract.Columns.NUM_COMMENTS)
        val num_comments: Int? = 0,

        @ColumnInfo(name = DataBaseContract.RedditPostContract.Columns.CREATED_AT)
        @Json(name = "created")
        val createdAt: Long,

        @ColumnInfo(name = DataBaseContract.RedditPostContract.Columns.SUBREDDIT_SELF_TEXT)
        @Json(name = "selftext")
        val subSelfText: String? = "",

        @ColumnInfo(name = DataBaseContract.RedditPostContract.Columns.SCORE)
        val score: Int? = 0,

        @ColumnInfo(name = DataBaseContract.RedditPostContract.Columns.URL)
        val url: String? = "",

        @Transient
        @ColumnInfo(name = DataBaseContract.RedditPostContract.Columns.CATEGORY)
        var postCategory: String = "",
        var isExpandable: Boolean = false,

        @ColumnInfo(name = DataBaseContract.RedditPostContract.Columns.IS_SUBSCRIBED)
        var isSubscribed: Boolean = false
    ) : Parcelable, RedditItem()

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class RedditComment(
        val name: String ,
        val author: String? = "anonymous",
        @Json(name = "created")
        val createdAt: Long? = 0,
        val body: String? = "",
        val replies: List<RedditComment>?= emptyList(),
        var isSubscribed: Boolean? = null,
        var isExpandable: Boolean = false,
        ) : Parcelable, RedditItem()

    @JsonClass(generateAdapter = true)
    data class RedditCommentRaw(

        val name: String,
        val author: String? = "anonymous",
        @Json(name = "created")
        val createdAt: Long? = 0,
        val body: String? = "",
        @Json(name = "replies")
        val repliesObj: Any?,
        var isSubscribed: Boolean? = null,
        var isExpandable: Boolean = false,
        ) :
        RedditItem()
}