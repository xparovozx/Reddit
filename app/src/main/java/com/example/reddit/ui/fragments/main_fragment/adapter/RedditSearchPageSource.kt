package com.example.reddit.ui.fragments.main_fragment.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.reddit.data.RedditItem
import com.example.reddit.networking.RedditApi
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class RedditSearchPageSource (private val redditApi: RedditApi,
                              private val query : String
) : PagingSource<String, RedditItem.RedditPost>() {

    override suspend fun load(params: PagingSource.LoadParams<String>): PagingSource.LoadResult<String, RedditItem.RedditPost> {
        return try {
            val dataResponse = redditApi.getSearchNews(
                theme = query ,
                after = if (params is LoadParams.Append) params.key else null,
                before = if (params is LoadParams.Prepend) params.key else null,
                limit = params.loadSize
            ).dataResponse
            Timber.tag("PagingKey").d("Paging Subreddit AFTER = ${dataResponse.after}")
            Timber.tag("PagingKey").d("Paging Subreddit BEFORE = ${dataResponse.before}")
            val subscribedResponse = redditApi.getSubscribes()
            val listOfSubscribed = subscribedResponse.dataResponse.children.map {
                it.redditItem.name
            }
            val subredditsList = dataResponse.children.map {
                it.redditItem
            }.also {subredditsList ->
                subredditsList.map { redditPost ->
                    redditPost.isSubscribed = listOfSubscribed.contains(redditPost.subredditId)
                }
            }
            LoadResult.Page(
                data = subredditsList ,
                nextKey = dataResponse.after,
                prevKey = dataResponse.before
            )
        } catch (e: IOException) {
            Timber.tag("LoadPageError").e("LoadPageError = $e")
            LoadResult.Error(e)
        } catch (e: HttpException) {
            Timber.tag("LoadPageError").e("LoadPageError = $e")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, RedditItem.RedditPost>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}