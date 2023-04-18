package com.example.reddit.ui.fragments.comments

import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.reddit.R
import com.example.reddit.data.MainRepository
import com.example.reddit.data.RedditItem
import com.example.reddit.data.RedditItemUIState
import com.example.reddit.data.UserRepository
import com.example.reddit.utils.Constants.DEFAULT_PAGE_SIZE
import com.example.reddit.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val subsRepo: MainRepository,
    private val userRepo: UserRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _commentUIState: MutableStateFlow<RedditItemUIState> = MutableStateFlow(
        RedditItemUIState.Success(PagingData.empty())
    )
    val commentUIState: MutableStateFlow<RedditItemUIState>
        get() = _commentUIState
    init {
        if (!savedStateHandle.contains(KEY_SUBCOMMENT)) {
            savedStateHandle.set(KEY_SUBCOMMENT, DEFAULT_SUBCOMMENT)
        }
    }
    val commentMovedSuccess = SingleLiveEvent<Int>()
    val commentMoveError = SingleLiveEvent<Int>()
    private val clearListChannel = Channel<Unit>(Channel.CONFLATED)

    fun saveOrUnsaveComment(comment: RedditItem.RedditComment) {
        viewModelScope.launch {
            when (comment.isSubscribed) {
                true -> unSaveCommentForUser(comment)
                false -> saveCommentForUser(comment)
                else -> {
                    comment.isSubscribed = false
                    commentMoveError.postValue(R.string.snackbar_unknown_add_error)
                }
            }
        }
    }

    private suspend fun saveCommentForUser(comment: RedditItem.RedditComment) {
        try {
            userRepo.saveCommentForUser(comment)
            commentMovedSuccess.postValue(R.string.snackbar_comment_added)
        } catch (t: Throwable) {
            comment.isSubscribed = false
            commentMoveError.postValue(R.string.snackbar_comment_add_error)
        }
    }

    private suspend fun unSaveCommentForUser(comment: RedditItem.RedditComment) {
        try {
            userRepo.unSaveCurrentCommentForUser(comment)
            commentMovedSuccess.postValue(R.string.snackbar_comment_removed)
        } catch (t: Throwable) {
            comment.isSubscribed = true
            commentMoveError.postValue(R.string.snackbar_comment_remove_error)
        }
    }

    @ExperimentalPagingApi
    @ExperimentalCoroutinesApi
    @FlowPreview
    fun getComments(article: String, pageSize: Int? = DEFAULT_PAGE_SIZE) {
        val commentsFlow = flowOf(
            clearListChannel.receiveAsFlow().map {
                PagingData.empty<RedditItem.RedditComment>()
            },
            savedStateHandle.getLiveData<String>(KEY_SUBCOMMENT)
                .asFlow()
                .flatMapLatest {
                    subsRepo.getCommentsForSub(
                        article = article,
                        pageSize = pageSize ?: DEFAULT_PAGE_SIZE
                    )
                }.cachedIn(viewModelScope)
                .flowOn(Dispatchers.IO)
                .catch {}
        ).flattenMerge(concurrency = DEFAULT_CONCURRENCY)
        viewModelScope.launch {
            commentsFlow.collectLatest {
                _commentUIState.value = RedditItemUIState.Success(it)
            }
        }
    }

    @ExperimentalPagingApi
    @FlowPreview
    @ExperimentalCoroutinesApi
    val comments = getComments(article = savedStateHandle.get<String>(ARTICLE_KEY) ?: "")

    companion object {
        const val KEY_SUBCOMMENT = "subcomment"
        const val DEFAULT_SUBCOMMENT = "androiddev_comment"
        const val ARTICLE_KEY = "idArticle"
    }
}