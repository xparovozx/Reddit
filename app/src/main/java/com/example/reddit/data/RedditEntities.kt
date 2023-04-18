package com.example.reddit.data

import android.os.Parcelable
import androidx.room.*
import com.example.reddit.db.DataBaseContract
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

enum class PostsType {
    NEWS, TOP, SEARCH, USER_FAVORITES, REDDITOR_SUBS
}

@Parcelize
@JsonClass(generateAdapter = true)
@Entity(
    tableName = DataBaseContract.RedditorContract.RedditorSub.TABLE_NAME
)
data class RedditorInfo(

    @PrimaryKey(autoGenerate = true)
    @Transient
    @ColumnInfo(name = DataBaseContract.RedditorContract.RedditorSub.Columns.ID)
    val id: Long = 0,

    @ColumnInfo(name = DataBaseContract.RedditorContract.RedditorSub.Columns.ID_SUBREDDIT)
    val subredditId: String? = "",

    @Json(name = "display_name_prefixed")
    @ColumnInfo(name = DataBaseContract.RedditorContract.RedditorSub.Columns.NAME_PREFIXED)
    val namePrefixed: String,

    @Json(name = "banner_img")
    @ColumnInfo(name = DataBaseContract.RedditorContract.RedditorSub.Columns.AVATAR_IMG)
    val avatarImage: String

) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class FriendMade(
    val name: String,
    val note: String?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class User(
    val name: String,
    @Json(name = "icon_img")
    val avatarImage: String,
    val subreddit: UserSubreddit,
    ) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class UserSubreddit(
    @Json(name = "display_name_prefixed")
    val displayName: String

) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class UserFriends(
    val name: String
) : Parcelable

@JsonClass(generateAdapter = true)
data class ItemResponseWrapper<T>(
    @Json(name = "data")
    val dataResponse: T
)

@JsonClass(generateAdapter = true)
data class ItemChildrenDataWrapper<D>(
    val children: List<D>,
    val after: String?,
    val before: String?
)

@JsonClass(generateAdapter = true)
data class RedditItemsWrapper<I>(
    val kind: String,
    @Json(name = "data")
    val redditItem: I
)