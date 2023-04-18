package com.example.reddit.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.reddit.db.RedditDataBase
import com.example.reddit.db.RedditPostRemoteMediator
import com.example.reddit.db.RedditorRemoteMediator
import com.example.reddit.di.DispatcherIO
import com.example.reddit.networking.RedditApi
import com.example.reddit.ui.fragments.favorites.FavoriteCommentsPageSource
import com.example.reddit.ui.fragments.favorites.FavoriteSavedCommentsPageSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    val redditApi: RedditApi,
    val dataBase: RedditDataBase,
    @DispatcherIO val contextDispatcher : CoroutineDispatcher
) : UserRepository {
    override suspend fun getUserIdentity(): User {
        return withContext(contextDispatcher) {
            redditApi.getUserIdentity()
        }
    }

    override suspend fun getUserSubs(userName: String): Int {
        return withContext(contextDispatcher) {
            redditApi.getRedditorSubs(author = userName).dataResponse.children.size
        }
    }

    override suspend fun getUserCommentsSize(userName: String): Int {
        return withContext(contextDispatcher) {
            redditApi.getUserComments(userName).dataResponse.children.size
        }
    }
    @ExperimentalPagingApi
    override suspend fun getUserComments(pageSize: Int): Flow<PagingData<RedditItem.RedditComment>> {
        return Pager(PagingConfig(pageSize)) {
            FavoriteCommentsPageSource(redditApi)
        }.flow
    }

    @ExperimentalPagingApi
    override suspend fun getUserFavorites(pageSize: Int): Flow<PagingData<RedditItem.RedditPost>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                prefetchDistance = pageSize,
                initialLoadSize = pageSize * 3,
                maxSize = pageSize * 4,
                enablePlaceholders = false
            ),
            remoteMediator = RedditPostRemoteMediator(
                redditApi = redditApi,
                dataBase = dataBase,
                postsCategory = PostsType.USER_FAVORITES
            )
        ) {
            dataBase.redditPostDao().getPosts(PostsType.USER_FAVORITES.name)
        }.flow
    }

    @ExperimentalPagingApi
    override suspend fun getUsersFriendsList(pageSize: Int): Flow<PagingData<RedditItem.Redditor>> {
        return Pager(PagingConfig(pageSize),
            remoteMediator = RedditorRemoteMediator(
                redditApi = redditApi,
                dataBase = dataBase,
            )) {
            dataBase.redditorDao().getAllRedditorsWithRedditorInfo()
        }.flow
    }

    override suspend fun saveCommentForUser(comment: RedditItem.RedditComment) {
        withContext(contextDispatcher) {
            try {
                redditApi.saveComment("comment", comment.name)
                comment.isSubscribed = true
            } catch (t: Throwable) {
                Timber.tag("SaveCommentError").d("Comment save error = $t")
                comment.isSubscribed = false
            }
        }
    }

    override suspend fun unSaveCurrentCommentForUser(comment: RedditItem.RedditComment) {
        withContext(contextDispatcher) {
            try {
                redditApi.unSaveComment(commentFullName = comment.name)
                comment.isSubscribed = false
            } catch (t: Throwable) {
                Timber.tag("SaveCommentError").d("Comment save error = $t")
                comment.isSubscribed = true
            }
        }
    }

    override suspend fun unSaveAllCommentsForUser() {
        withContext(contextDispatcher) {
            val userName = redditApi.getUserIdentity().name
            val commentNames =
                redditApi.getSavedComments(userName).dataResponse.children.map { it.redditItem.name }
            commentNames.forEach { commentName ->
                redditApi.unSaveComment(commentName)
            }
        }
    }

    override suspend fun unSaveSubForUser() {
        withContext(contextDispatcher) {
            val userName = redditApi.getUserIdentity().name
            val subNames =
                redditApi.getSavedSubs(userName).dataResponse.children.map { it.redditItem.name }
            subNames.forEach { redditLink ->
                redditApi.unSaveSubreddit(redditLink)
            }
        }
    }
    @ExperimentalPagingApi
    override suspend fun getSavedComments(pageSize: Int): Flow<PagingData<RedditItem.RedditComment>> {
        return Pager(PagingConfig(pageSize)) {
            FavoriteSavedCommentsPageSource(redditApi)
        }.flow
    }
}