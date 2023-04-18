package com.example.reddit.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.reddit.db.RedditDataBase
import com.example.reddit.db.RedditPostRemoteMediator
import com.example.reddit.di.DispatcherIO
import com.example.reddit.networking.RedditApi
import com.example.reddit.ui.fragments.comments.adapter.CommentPageSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    val redditApi: RedditApi,
    val dataBase: RedditDataBase,
    @DispatcherIO val contextDispatcher: CoroutineDispatcher
) : MainRepository {

    @ExperimentalPagingApi
    override suspend fun getCommentsForSub(
        article: String,
        pageSize: Int
    ): Flow<PagingData<RedditItem.RedditComment>> {
        return withContext(contextDispatcher) {
            Pager(
                config = PagingConfig(
                    pageSize = pageSize,
                    prefetchDistance = pageSize * 2,
                    initialLoadSize = pageSize,
                    maxSize = pageSize * 5,
                    enablePlaceholders = false
                )
            ) {
                CommentPageSource(
                    redditApi = redditApi,
                    article = article
                )
            }.flow
        }
    }

    @ExperimentalPagingApi
    override suspend fun getNews(pageSize: Int): Flow<PagingData<RedditItem.RedditPost>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                prefetchDistance = pageSize * 2,
                initialLoadSize = pageSize + 3,
                maxSize = pageSize * 5,
                enablePlaceholders = false
            ),
            remoteMediator = RedditPostRemoteMediator(
                redditApi = redditApi,
                dataBase = dataBase,
                postsCategory = PostsType.NEWS
            )
        ) {
            dataBase.redditPostDao().getPosts(PostsType.NEWS.name)
        }.flow
    }

    @ExperimentalPagingApi
    override suspend fun getTopNews(pageSize: Int): Flow<PagingData<RedditItem.RedditPost>> {
        return Pager(
            PagingConfig(
                pageSize = pageSize,
                prefetchDistance = pageSize * 2,
                initialLoadSize = pageSize + 3,
                maxSize = pageSize * 5,
                enablePlaceholders = false
            ),
            remoteMediator = RedditPostRemoteMediator(
                redditApi = redditApi,
                dataBase = dataBase,
                postsCategory = PostsType.TOP
            )
        ) {
            dataBase.redditPostDao().getPosts(PostsType.TOP.name)
        }.flow
    }

    @ExperimentalPagingApi
    override suspend fun getSearchNews(
        pageSize: Int,
        query: String
    ): Flow<PagingData<RedditItem.RedditPost>> {

        return Pager(
            PagingConfig(
                pageSize = pageSize,
                prefetchDistance = pageSize * 2,
                initialLoadSize = pageSize + 3,
                maxSize = pageSize * 5,
                enablePlaceholders = false
            ),
            remoteMediator = RedditPostRemoteMediator(
                redditApi = redditApi,
                dataBase = dataBase,
                postsCategory = PostsType.SEARCH,
                query = query
            )
        ) {
            dataBase.redditPostDao().getSearchedNews(query)

        }.flow.flowOn(contextDispatcher)
    }


    override suspend fun saveSubreddit(subreddit: RedditItem.RedditPost) {
        withContext(contextDispatcher) {
            try {
                redditApi.saveSubreddit(subFullName = subreddit.name)
                subreddit.isSubscribed = true
                dataBase.redditPostDao().updatePosts(listOf(subreddit))
            } catch (t: Throwable) {
                Timber.tag("SubscribeError").d("Subscribe error = $t")
            }
        }
    }

    override suspend fun unSaveSubreddit(subreddit: RedditItem.RedditPost) {
        withContext(contextDispatcher) {
            try {
                redditApi.unSaveSubreddit(subFullName = subreddit.name)
                subreddit.isSubscribed = false
                dataBase.redditPostDao().updatePosts(listOf(subreddit))
            } catch (t: Throwable) {
                Timber.tag("SubscribeError").d("Subscribe error = $t")
            }
        }
    }
}