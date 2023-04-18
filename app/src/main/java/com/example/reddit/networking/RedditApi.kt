package com.example.reddit.networking

import com.example.reddit.data.*
import com.example.reddit.di.CommentsWithReplies
import com.example.reddit.utils.Constants.DEFAULT_PAGE_SIZE
import retrofit2.http.*

interface RedditApi {
    @GET("/r/popular/top")
    suspend fun getTopNews(
        @Query("after") after: String? = null,
        @Query("before") before: String? = null,
        @Query("limit") limit: Int = DEFAULT_PAGE_SIZE,
        @Query("count") count: Int = DEFAULT_PAGE_SIZE
    ): ItemResponseWrapper<ItemChildrenDataWrapper<RedditItemsWrapper<RedditItem.RedditPost>>>

    @GET("/r/popular/new")
    suspend fun getNews(
        @Query("after") after: String? = null,
        @Query("before") before: String? = null,
        @Query("limit") limit: Int = DEFAULT_PAGE_SIZE,
        @Query("count") count: Int = DEFAULT_PAGE_SIZE
    ): ItemResponseWrapper<ItemChildrenDataWrapper<RedditItemsWrapper<RedditItem.RedditPost>>>

    @GET("/r/{theme}/new")
    suspend fun getSearchNews(
        @Path(value = "theme") theme: String? = "",
        @Query("after") after: String? = null,
        @Query("before") before: String? = null,
        @Query("limit") limit: Int = DEFAULT_PAGE_SIZE,
        @Query("count") count: Int = DEFAULT_PAGE_SIZE
    ): ItemResponseWrapper<ItemChildrenDataWrapper<RedditItemsWrapper<RedditItem.RedditPost>>>

    @POST("/api/save")
    suspend fun saveSubreddit(
        @Query(value = "category") category: String = "link",
        @Query(value = "id") subFullName: String
    )

    @POST("/api/unsave")
    suspend fun unSaveSubreddit(
        @Query(value = "id") subFullName: String
    )

    @GET("/user/{username}/saved")
    suspend fun getSavedSubs(
        @Path(value = "username") userName: String,
        @Query("after") after: String? = null,
        @Query("before") before: String? = null,
        @Query("limit") limit: Int = DEFAULT_PAGE_SIZE,
        @Query("type") type: String = "links"
    ): ItemResponseWrapper<ItemChildrenDataWrapper<RedditItemsWrapper<RedditItem.RedditPost>>>

    @GET("/subreddits/mine/subscriber")
    suspend fun getSubscribes(
        @Query("after") after: String? = null,
        @Query("before") before: String? = null,
        @Query("limit") limit: Int = DEFAULT_PAGE_SIZE,
        @Query("count") count: Int = DEFAULT_PAGE_SIZE,
        @Query("sr_detail") srDetail: String = "all"
    ): ItemResponseWrapper<ItemChildrenDataWrapper<RedditItemsWrapper<RedditItem.RedditPost>>>

    @GET("/comments/{article}")
    @CommentsWithReplies
    suspend fun getSubsComments(
        @Path(value = "article") article: String,
        @Query(value = "sort") sort: String = "new",
        @Query("limit") limit: Int = DEFAULT_PAGE_SIZE,
    ):List<RedditItem.RedditComment>

    @GET("/api/morechildren")
    suspend fun getMoreCommentsChildren(
        @Query("link") article: String,
        @Query("children") children: List<String>,
        @Query("sort") sort: String = "new",
        ): List<ItemResponseWrapper<ItemChildrenDataWrapper<RedditItemsWrapper<RedditItem.RedditComment>>>>

    @GET("/user/{username}/about")
    suspend fun getRedditorInfo(
        @Path(value = "username") redditorName: String,
    ): ItemResponseWrapper<RedditItem.Redditor>

    @GET("/user/{author}/submitted")
    suspend fun getRedditorSubs(
        @Path(value = "author") author: String,
        @Query("after") after: String? = null,
        @Query("before") before: String? = null,
        @Query("limit") limit: Int = DEFAULT_PAGE_SIZE,
        @Query(value = "show") show: String = "all",
        @Query(value = "sort") sort: String = "relevance",
    ): ItemResponseWrapper<ItemChildrenDataWrapper<RedditItemsWrapper<RedditItem.RedditPost>>>

    @PUT("/api/v1/me/friends/{username}")
    suspend fun makeFriend(
        @Path(value = "username") redditorName: String,
        @Body username: FriendMade
    )

    @DELETE("/api/v1/me/friends/{username}")
    suspend fun unFriend(
        @Path(value = "username") redditorName: String
    )

    @GET("/api/v1/me/friends")
    suspend fun getUserFriendsList(
        @Query("after") after: String? = null,
        @Query("before") before: String? = null,
        @Query("limit") limit: Int = DEFAULT_PAGE_SIZE,
        @Query("count") count: Int = DEFAULT_PAGE_SIZE
    ): ItemResponseWrapper<ItemChildrenDataWrapper<UserFriends>>

    @GET("/api/v1/me")
    suspend fun getUserIdentity(): User

    @GET("/user/{username}/comments")
    suspend fun getUserComments(
        @Path(value = "username") userName: String,
        @Query("after") after: String? = null,
        @Query("before") before: String? = null,
        @Query("limit") limit: Int = 100,
        @Query("count") count: Int = 100
    ): ItemResponseWrapper<ItemChildrenDataWrapper<RedditItemsWrapper<RedditItem.RedditComment>>>

    @POST("/api/save")
    suspend fun saveComment(
        @Query(value = "category") category: String = "comment",
        @Query(value = "id") commentFullName: String
    )

    @POST("/api/unsave")
    suspend fun unSaveComment(
        @Query(value = "id") commentFullName: String
    )

    @GET("/user/{username}/saved")
    suspend fun getSavedComments(
        @Path(value = "username") userName: String,
        @Query("after") after: String? = null,
        @Query("before") before: String? = null,
        @Query("limit") limit: Int = 100,
        @Query("type") type: String = "comments"
    ): ItemResponseWrapper<ItemChildrenDataWrapper<RedditItemsWrapper<RedditItem.RedditComment>>>
}