package com.example.reddit.ui.fragments.favorites

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.example.reddit.data.RedditItem
import com.example.reddit.networking.RedditApi
import timber.log.Timber
import java.io.IOException

class FavoriteCommentsPageSource(private val redditApi: RedditApi) :
    PagingSource<String, RedditItem.RedditComment>() {

    override fun getRefreshKey(state: PagingState<String, RedditItem.RedditComment>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, RedditItem.RedditComment> {
        return try {
            val userName = redditApi.getUserIdentity().name
            val userCommentsResponse = redditApi.getUserComments(userName)
            val userSavedCommentsResponse = redditApi.getSavedComments(userName)
            val savedCommentsNamesList = userSavedCommentsResponse.dataResponse.children.map {
                it.redditItem.name
            }
            val commentsList = userCommentsResponse.dataResponse.children.map {
                it.redditItem
            }.also {
                it.map {
                        redditComment ->
                    redditComment.isSubscribed = savedCommentsNamesList.contains(redditComment.name)
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