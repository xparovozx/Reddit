package com.example.reddit.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface RedditorRepository {
    suspend fun getRedditor(redditorName : String) : RedditItem.Redditor
    @ExperimentalPagingApi
    suspend fun getRedditorSubs(redditorName: String, pageSize: Int) : Flow<PagingData<RedditItem.RedditPost>>
    suspend fun makeFriend(redditorName: String)
    suspend fun unFriend(redditorName: String)
    suspend fun getFriendRelation(redditorName: String) : Boolean
}