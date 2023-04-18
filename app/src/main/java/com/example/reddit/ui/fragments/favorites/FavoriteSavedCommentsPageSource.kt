package com.example.reddit.ui.fragments.favorites

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.example.reddit.data.RedditItem
import com.example.reddit.networking.RedditApi
import timber.log.Timber
import java.io.IOException

class FavoriteSavedCommentsPageSource(private val redditApi: RedditApi) :
    PagingSource<String, RedditItem.RedditComment>() {

    override fun getRefreshKey(state: PagingState<String, RedditItem.RedditComment>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, RedditItem.RedditComment> {
        return try {
            val userName = redditApi.getUserIdentity().name
            val userCommentsResponse = redditApi.getSavedComments(userName)
            val commentsList = userCommentsResponse.dataResponse.children.map {
                it.redditItem
            }.also{
                    listSubscribedComments->
                listSubscribedComments.map {
                    it.isSubscribed = true
                }
            }
            PagingSource.LoadResult.Page(
                data = commentsList,
                nextKey = null,
                prevKey = null
            )
        } catch (e: IOException) {
            Timber.tag("LoadPageError").e("LoadPageError = $e")
            PagingSource.LoadResult.Error(e)
        } catch (e: HttpException) {
            Timber.tag("LoadPageError").e("LoadPageError = $e")
            PagingSource.LoadResult.Error(e)
        }
    }
}