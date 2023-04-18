package com.example.reddit.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserIdentity() : User
    suspend fun getUserSubs(userName : String) : Int
    suspend fun getUserCommentsSize(userName: String) : Int
    @ExperimentalPagingApi
    suspend fun getUserComments(pageSize : Int): Flow<PagingData<RedditItem.RedditComment>>
    @ExperimentalPagingApi
    suspend fun getUserFavorites(pageSize : Int): Flow<PagingData<RedditItem.RedditPost>>
    @ExperimentalPagingApi
    suspend fun getUsersFriendsList(pageSize: Int): Flow<PagingData<RedditItem.Redditor>>
    suspend fun saveCommentForUser(comment : RedditItem.RedditComment)
    suspend fun unSaveCurrentCommentForUser(comment: RedditItem.RedditComment)
    suspend fun unSaveAllCommentsForUser()
    suspend fun unSaveSubForUser()
    @ExperimentalPagingApi
    suspend fun getSavedComments(pageSize : Int): Flow<PagingData<RedditItem.RedditComment>>
}