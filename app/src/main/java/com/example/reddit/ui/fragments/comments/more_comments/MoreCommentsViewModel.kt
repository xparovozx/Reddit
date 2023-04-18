package com.example.reddit.ui.fragments.comments.more_comments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.reddit.R
import com.example.reddit.data.*
import com.example.reddit.utils.Constants
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
class MoreCommentsViewModel @Inject constructor(
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
        if (!savedStateHandle.contains(KEY_MORE_SUBCOMMENT)) {
            savedStateHandle.set(KEY_MORE_SUBCOMMENT, DEFAULT_MORE_SUBCOMMENT)
        }
    }

    val commentMovedSuccess = SingleLiveEvent<Int>()
    val commentMoveError = SingleLiveEvent<Int>()
    private val clearListChannel = Channel<Unit>(Channel.CONFLATED)

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

    @ExperimentalPagingApi
    @ExperimentalCoroutinesApi
    @FlowPreview
    fun getComments(article: String, pageSize : Int? = Constants.DEFAULT_PAGE_SIZE) {
        val commentsFlow = flowOf(
            clearListChannel.receiveAsFlow().map {
                PagingData.empty<RedditItem.RedditComment>()
            },
            savedStateHandle.getLiveData<String>(KEY_MORE_SUBCOMMENT)
                .asFlow()
                .flatMapLatest {
                    subsRepo.getCommentsForSub(
                        article = article,
                        pageSize = pageSize ?: Constants.DEFAULT_PAGE_SIZE
                    )
                }.cachedIn(viewModelScope)
                .flowOn(Dispatchers.IO)
        ).flattenMerge(concurrency = DEFAULT_CONCURRENCY)
        viewModelScope.launch {
            commentsFlow.collectLatest {pagingCommentData->
                _commentUIState.value = RedditItemUIState.Success(pagingCommentData)
            }
        }
    }

    @ExperimentalPagingApi
    @FlowPreview
    @ExperimentalCoroutinesApi
    val commentsPagingData = getComments(article = savedStateHandle.get<String>(ARTICLE_KEY) ?: "")

    companion object {
        const val KEY_MORE_SUBCOMMENT = "more_subcomment"
        const val DEFAULT_MORE_SUBCOMMENT = "androiddev_more_comment"
        const val ARTICLE_KEY = "subredditId"
    }
}