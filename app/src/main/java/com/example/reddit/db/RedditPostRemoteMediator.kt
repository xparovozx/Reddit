package com.example.reddit.db

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.reddit.data.*
import com.example.reddit.networking.RedditApi
import retrofit2.HttpException
import java.io.IOException

@ExperimentalPagingApi
@OptIn(ExperimentalPagingApi::class)
class RedditPostRemoteMediator(
    private val redditApi: RedditApi,
    private val dataBase: RedditDataBase,
    private val postsCategory: PostsType,
    private val authorName: String? = "",
    private val query: String? = ""
) : RemoteMediator<Int, RedditItem.RedditPost>() {
    private val postDao = dataBase.redditPostDao()
    private val remoteKeyDao = dataBase.redditPostRemoteKeyDao()
    private lateinit var apiResponse: ItemResponseWrapper<ItemChildrenDataWrapper<RedditItemsWrapper<RedditItem.RedditPost>>>
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RedditItem.RedditPost>
    ): MediatorResult {
        try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = dataBase.withTransaction {
                        remoteKeyDao.getRedditPostRemoteKeys(postCategory = postsCategory.name)
                    }
                    if (remoteKey?.nextKey == null) return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    remoteKey.nextKey
                }
            }

            val userName = redditApi.getUserIdentity().name
            val subscribedResponse = redditApi.getSavedSubs(userName = userName)
            val listOfSubscribed = subscribedResponse.dataResponse.children.map {
                it.redditItem.name
            }
            when (postsCategory) {
                PostsType.NEWS -> {
                    apiResponse = redditApi.getNews(
                        after = loadKey,
                        before = null,
                        limit = when (loadType) {
                            LoadType.REFRESH -> state.config.initialLoadSize
                            else -> state.config.pageSize
                        }
                    )
                }
                PostsType.TOP -> {
                    apiResponse = redditApi.getTopNews(
                        after = loadKey,
                        before = null,
                        limit = when (loadType) {
                            LoadType.REFRESH -> state.config.initialLoadSize
                            else -> state.config.pageSize
                        }
                    )
                }
                PostsType.SEARCH -> {
                    apiResponse = redditApi.getSearchNews(
                        after = loadKey,
                        before = null,
                        limit = when (loadType) {
                            LoadType.REFRESH -> state.config.initialLoadSize
                            else -> state.config.pageSize
                        }, theme = query
                    )
                }
                PostsType.USER_FAVORITES -> {
                    apiResponse = redditApi.getSavedSubs(
                        userName = userName,
                        after = loadKey,
                        before = null,
                        limit = when (loadType) {
                            LoadType.REFRESH -> state.config.initialLoadSize
                            else -> state.config.pageSize
                        }
                    )
                }
                PostsType.REDDITOR_SUBS -> {
                    apiResponse = redditApi.getRedditorSubs(
                        author = authorName ?: "",
                        after = loadKey,
                        before = null,
                        limit = when (loadType) {
                            LoadType.REFRESH -> state.config.initialLoadSize
                            else -> state.config.pageSize
                        }
                    )
                }
            }

            val redditPosts = apiResponse.dataResponse.children.map {
                it.redditItem
            }.also { subredditsList ->
                subredditsList.map { redditPost ->
                    redditPost.isSubscribed = listOfSubscribed.contains(redditPost.name)
                    redditPost.postCategory = postsCategory.name
                }
            }
            dataBase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    postDao.cleanRedditTableByCategory(postCategory = postsCategory.name)
                    remoteKeyDao.clearRemoteKeys(postsCategory.name)
                }
                remoteKeyDao.insertAll(
                    listOf(
                        RemotePostKeys(
                            postsCategory.name,
                            apiResponse.dataResponse.after
                        )
                    )
                )
                postDao.insertPosts(redditPosts)
            }
            return MediatorResult.Success(endOfPaginationReached = redditPosts.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}