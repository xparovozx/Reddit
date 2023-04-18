package com.example.reddit.ui.fragments.comments.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.reddit.data.RedditItem
import com.example.reddit.networking.RedditApi
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class CommentPageSource(
    private val article: String,
    private val redditApi: RedditApi
) : PagingSource<String, RedditItem.RedditComment>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, RedditItem.RedditComment> {
        return try {
            val commentsList = redditApi.getSubsComments(
                article = article,
                limit = params.loadSize
            )
            val userName = redditApi.getUserIdentity().name
            val userSavedCommentsResponse = redditApi.getSavedComments(userName)
            val savedCommentsNamesList = userSavedCommentsResponse.dataResponse.children.map {
                it.redditItem.name
            }
            commentsList.filter { it.author != "anonymous" }.map { redditComment ->
                redditComment.isSubscribed = savedCommentsNamesList.contains(redditComment.name)
            }
            LoadResult.Page(
                data = commentsList,
                nextKey = null,
                prevKey = null
            )
        } catch (e: IOException) {
            Timber.tag("LoadPageError").e("LoadPageError = $e")
            LoadResult.Error(e)
        } catch (e: HttpException) {
            Timber.tag("LoadPageError").e("LoadPageError = $e")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, RedditItem.RedditComment>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}