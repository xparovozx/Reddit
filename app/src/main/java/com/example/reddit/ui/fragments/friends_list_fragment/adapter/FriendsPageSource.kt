package com.example.reddit.ui.fragments.friends_list_fragment.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.reddit.data.RedditItem
import com.example.reddit.networking.RedditApi
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class FriendsPageSource(
    private val redditApi : RedditApi
) : PagingSource<String, RedditItem.Redditor>() {
    override fun getRefreshKey(state: PagingState<String, RedditItem.Redditor>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, RedditItem.Redditor> {
        return try {
            val listOfFriends = mutableListOf<RedditItem.Redditor>()
            val userFriendChildren = redditApi.getUserFriendsList(
                after = if (params is LoadParams.Append) params.key else null,
                before = if (params is LoadParams.Prepend) params.key else null,
                limit = params.loadSize
            ).dataResponse
            val friendNamesList= userFriendChildren.children.map { it.name }
            friendNamesList.forEach {
                val redditor =  redditApi.getRedditorInfo(redditorName = it).dataResponse
                listOfFriends.add(redditor)
            }
            LoadResult.Page(
                data= listOfFriends,
                prevKey = userFriendChildren.after,
                nextKey = userFriendChildren.before
            )
        }catch (e: IOException) {
            Timber.tag("LoadPageError").e("LoadPageError = $e")
            LoadResult.Error(e)
        } catch (e: HttpException) {
            Timber.tag("LoadPageError").e("LoadPageError = $e")
            LoadResult.Error(e)
        }
    }
}