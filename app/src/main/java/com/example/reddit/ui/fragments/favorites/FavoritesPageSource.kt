package com.example.reddit.ui.fragments.favorites

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.reddit.data.RedditItem
import com.example.reddit.networking.RedditApi
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class FavoritesPageSource(
    private val redditApi: RedditApi
) : PagingSource<String, RedditItem.RedditPost>() {

    override suspend fun load(params: PagingSource.LoadParams<String>): PagingSource.LoadResult<String, RedditItem.RedditPost> {
        return try {
            val userName = redditApi.getUserIdentity().name
            val subscribedResponse = redditApi.getSavedSubs(userName = userName)
            val subscribesList = subscribedResponse.dataResponse.children.map {
                it.redditItem
            }.also {subscribedList->
                subscribedList.map {
                    it.isSubscribed = true
                }
            }
            PagingSource.LoadResult.Page(
                data = subscribesList,
                nextKey = subscribedResponse.dataResponse.after,
                prevKey = subscribedResponse.dataResponse.before
            )
        } catch (e: IOException) {
            Timber.tag("LoadPageError").e("LoadPageError = $e")
            PagingSource.LoadResult.Error(e)
        } catch (e: HttpException) {
            Timber.tag("LoadPageError").e("LoadPageError = $e")
            PagingSource.LoadResult.Error(e)
        }
    }

    override val keyReuseSupported: Boolean
        get() = true

    override fun getRefreshKey(state: PagingState<String, RedditItem.RedditPost>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}