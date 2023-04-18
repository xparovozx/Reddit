package com.example.reddit.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.example.reddit.data.RedditItem.RedditComment
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    @ExperimentalPagingApi
    suspend fun getNews(pageSize : Int) : Flow<PagingData<RedditItem.RedditPost>>

    @ExperimentalPagingApi
    suspend fun getTopNews(pageSize : Int) : Flow<PagingData<RedditItem.RedditPost>>
    suspend fun saveSubreddit(subreddit: RedditItem.RedditPost)
    suspend fun unSaveSubreddit(subreddit: RedditItem.RedditPost)

    @ExperimentalPagingApi
    suspend fun getCommentsForSub(
        article: String,
        pageSize : Int): Flow<PagingData<RedditComment>>

    @ExperimentalPagingApi
    suspend fun getSearchNews(pageSize: Int, query : String): Flow<PagingData<RedditItem.RedditPost>>
}