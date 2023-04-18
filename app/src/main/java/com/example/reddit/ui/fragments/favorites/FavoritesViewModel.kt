package com.example.reddit.ui.fragments.favorites

import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.reddit.R
import com.example.reddit.data.*
import com.example.reddit.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val subsRepo: MainRepository,
    private val userRepo: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val clearListChannel = Channel<Unit>(Channel.CONFLATED)
    val postMovedSuccess = SingleLiveEvent<Int>()
    val postMoveError = SingleLiveEvent<Int>()
    val commentMovedSuccess = SingleLiveEvent<Int>()
    val commentMoveError = SingleLiveEvent<Int>()

    init {
        if (!savedStateHandle.contains(KEY_SUBREDDIT))
            savedStateHandle.set(KEY_SUBREDDIT, DEFAULT_SUBREDDIT)
        if (!savedStateHandle.contains(KEY_SUBREDDIT_SELECTED))
            savedStateHandle.set(KEY_SUBREDDIT_SELECTED, DEFAULT_SUBREDDIT)
        if (!savedStateHandle.contains(KEY_SUBCOMMENT))
            savedStateHandle.set(KEY_SUBCOMMENT, DEFAULT_SUBREDDIT)
        if (!savedStateHandle.contains(KEY_SUBCOMMENT_SAVED))
            savedStateHandle.set(
                KEY_SUBCOMMENT_SAVED, DEFAULT_SUBREDDIT
            )
    }

    @ExperimentalPagingApi
    @FlowPreview
    @ExperimentalCoroutinesApi
    private  fun getCommentsSaved(): Flow<PagingData<RedditItem.RedditComment>> {
        return flowOf(
            clearListChannel.receiveAsFlow().map {
                PagingData.empty<RedditItem.RedditComment>()
            },
            savedStateHandle.getLiveData<String>(FavoritesViewModel.KEY_SUBCOMMENT)
                .asFlow()
                .flatMapLatest {
                    userRepo.getSavedComments(pageSize = DEFAULT_PAGE_SIZE)
                }.catch {
                }
                .cachedIn(viewModelScope)
                .flowOn(Dispatchers.IO)
        ).flattenMerge(DEFAULT_CONCURRENCY)
    }

    @ExperimentalPagingApi
    @ExperimentalCoroutinesApi
    @FlowPreview
    val savedComments = getCommentsSaved()

    @ExperimentalPagingApi
    @ExperimentalCoroutinesApi
    @FlowPreview
    private fun getCommentsByUser(): Flow<PagingData<RedditItem.RedditComment>> {
        return flowOf(
            clearListChannel.receiveAsFlow().map {
                PagingData.empty<RedditItem.RedditComment>()
            },
            savedStateHandle.getLiveData<String>(FavoritesViewModel.KEY_SUBCOMMENT)
                .asFlow()
                .flatMapLatest {
                    userRepo.getUserComments(
                        pageSize = DEFAULT_PAGE_SIZE
                    )
                }.catch {
                }
                .cachedIn(viewModelScope)
                .flowOn(Dispatchers.IO)
        ).flattenMerge(DEFAULT_CONCURRENCY)
    }

    @ExperimentalPagingApi
    @FlowPreview
    @ExperimentalCoroutinesApi
    val userComments = getCommentsByUser()

    @ExperimentalPagingApi
    @FlowPreview
    @ExperimentalCoroutinesApi
    private  fun getPostsSelected(): Flow<PagingData<RedditItem.RedditPost>> {
        return flowOf(
            clearListChannel.receiveAsFlow().map {
                PagingData.empty<RedditItem.RedditPost>()
            },
            savedStateHandle.getLiveData<String>(KEY_SUBREDDIT_SELECTED)
                .asFlow()
                .flatMapLatest {
                    userRepo.getUserFavorites(
                        pageSize = DEFAULT_PAGE_SIZE
                    )
                }.catch { }
                .cachedIn(viewModelScope)
                .flowOn(Dispatchers.IO)
        ).flattenMerge(DEFAULT_CONCURRENCY)
    }

    @ExperimentalPagingApi
    @ExperimentalCoroutinesApi
    @FlowPreview
    val postSelected = getPostsSelected()

    @ExperimentalPagingApi
    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun getAllPosts(): Flow<PagingData<RedditItem.RedditPost>> {
        return flowOf(
            clearListChannel.receiveAsFlow().map {
                PagingData.empty<RedditItem.RedditPost>()
            },
            savedStateHandle.getLiveData<String>(KEY_SUBREDDIT)
                .asFlow()
                .flatMapLatest {
                    subsRepo.getTopNews(
                        pageSize = DEFAULT_PAGE_SIZE
                    )
                }.catch { }
                .cachedIn(viewModelScope)
                .flowOn(Dispatchers.IO)
        ).flattenMerge(DEFAULT_CONCURRENCY)
    }

    @ExperimentalPagingApi
    @ExperimentalCoroutinesApi
    @FlowPreview
    val allFavoritePosts = getAllPosts()

    fun subOrUnsubscribe(subreddit: RedditItem.RedditPost) {
        viewModelScope.launch {
            when (subreddit.isSubscribed) {
                true -> {
                    try {
                        subsRepo.unSaveSubreddit(subreddit = subreddit)
                        postMovedSuccess.postValue(R.string.snackbar_post_removed)
                    } catch (t: Throwable) {
                        subreddit.isSubscribed = true
                        postMoveError.postValue(R.string.snackbar_post_remove_error)
                    }
                }
                false -> {
                    try {
                        subsRepo.saveSubreddit(subreddit = subreddit)
                        postMovedSuccess.postValue(R.string.snackbar_post_added)
                    } catch (t: Throwable) {
                        subreddit.isSubscribed = false
                        postMoveError.postValue(R.string.snackbar_post_add_error)
                    }
                }
            }
        }
    }

    fun saveOrUnsaveComment(comment: RedditItem.RedditComment) {
        viewModelScope.launch {
            when (comment.isSubscribed) {
                true -> {
                    try {
                        userRepo.unSaveCurrentCommentForUser(comment)
                        commentMovedSuccess.postValue(R.string.snackbar_comment_removed)
                    } catch (t: Throwable) {
                        comment.isSubscribed = true
                        commentMoveError.postValue(R.string.snackbar_comment_remove_error)
                    }
                }
                false -> {
                    try {
                        userRepo.saveCommentForUser(comment)
                        commentMovedSuccess.postValue(R.string.snackbar_comment_added)
                    } catch (t: Throwable) {
                        comment.isSubscribed = false
                        commentMoveError.postValue(R.string.snackbar_comment_add_error)
                    }
                }
                else -> {
                    comment.isSubscribed = false
                    commentMoveError.postValue(R.string.snackbar_unknown_add_error)
                }
            }
        }
    }

    companion object {
        private const val KEY_SUBCOMMENT = "subcomment"
        private const val KEY_SUBCOMMENT_SAVED = "subcomment_saved"
        private const val KEY_SUBREDDIT_SELECTED = "subreddit_selected"
        private const val KEY_SUBREDDIT = "subreddit"
        private const val DEFAULT_SUBREDDIT = "androiddev"
        private const val DEFAULT_PAGE_SIZE = 10
    }
}