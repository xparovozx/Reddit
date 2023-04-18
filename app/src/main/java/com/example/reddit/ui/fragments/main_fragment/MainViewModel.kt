package com.example.reddit.ui.fragments.main_fragment

import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.reddit.R
import com.example.reddit.data.MainRepository
import com.example.reddit.data.RedditItem
import com.example.reddit.data.RedditItemUIState
import com.example.reddit.utils.Constants.DEFAULT_PAGE_SIZE
import com.example.reddit.utils.SingleLiveEvent
import com.example.reddit.utils.collectItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val subsRepo: MainRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    init {
        if (!savedStateHandle.contains(KEY_SUBREDDIT_NEWS)) {
            savedStateHandle.set(KEY_SUBREDDIT_NEWS, DEFAULT_SUBREDDIT)
        }
        if (!savedStateHandle.contains(KEY_SUBREDDIT_POST)) {
            savedStateHandle.set(KEY_SUBREDDIT_POST, DEFAULT_SUBREDDIT)
        }
        if (!savedStateHandle.contains(KEY_SUBREDDIT_SEARCH)) {
            savedStateHandle.set(KEY_SUBREDDIT_SEARCH, DEFAULT_SUBREDDIT)
        }
    }
    private var _subredditJob = MutableLiveData<Job?>()
    private val subredditJob: LiveData<Job?>
        get() = _subredditJob
    private val _redditUIState:MutableStateFlow<RedditItemUIState> = MutableStateFlow(RedditItemUIState.Success(PagingData.empty()))
    val redditUIState: MutableStateFlow<RedditItemUIState>
        get() = _redditUIState
    val postMovedSuccess = SingleLiveEvent<Int>()
    val postMoveError = SingleLiveEvent<Int>()
    private val clearListChannel = Channel<Unit>(Channel.CONFLATED)
    private val postsNews = flowOf(
        clearListChannel.receiveAsFlow().map { PagingData.empty<RedditItem.RedditPost>() },
        savedStateHandle.getLiveData<String>(KEY_SUBREDDIT_NEWS)
            .asFlow()
            .flatMapLatest {
                subsRepo.getNews(
                    DEFAULT_PAGE_SIZE
                )
            }
            .cachedIn(viewModelScope)
            .flowOn(Dispatchers.IO)
    ).flattenMerge(DEFAULT_CONCURRENCY)
    init {
        _subredditJob.value = viewModelScope.launch {
            postsNews.collectItems(_redditUIState)
        }
    }
    private val topNews = flowOf(
        clearListChannel.receiveAsFlow().map { PagingData.empty<RedditItem.RedditPost>() },
        savedStateHandle.getLiveData<String>(KEY_SUBREDDIT_POST)
            .asFlow()
            .flatMapLatest {
                subsRepo.getTopNews(
                    DEFAULT_PAGE_SIZE
                )
            }
            .cachedIn(viewModelScope)
            .flowOn(Dispatchers.IO)
    ).flattenMerge(DEFAULT_CONCURRENCY)

    @Suppress("UNCHECKED_CAST")
    @JvmName("getTopNews1")
    @ExperimentalPagingApi
    @ExperimentalCoroutinesApi
    @FlowPreview
    fun getTopNews() {
        endJob()
        _subredditJob.value = viewModelScope.launch {
            topNews.collectItems(_redditUIState)
        }
    }
    @Suppress("UNCHECKED_CAST")
    @ExperimentalPagingApi
    @ExperimentalCoroutinesApi
    @FlowPreview
    fun getPostsNews() {
        endJob()
        _subredditJob.value = viewModelScope.launch {
            postsNews.collectItems(_redditUIState)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @ExperimentalPagingApi
    @FlowPreview
    @ExperimentalCoroutinesApi
    fun getPostsSearch(queryFlow: Flow<String>) {
        endJob()
        val searchPostsFlow = queryFlow.debounce(500)
            .distinctUntilChanged()
            .flatMapLatest {
                subsRepo.getSearchNews(
                    pageSize = DEFAULT_PAGE_SIZE, query = it
                )
            }
            .catch {}
            .cachedIn(viewModelScope)
            .flowOn(Dispatchers.IO)
        _subredditJob.value = viewModelScope.launch {
            searchPostsFlow.collectItems(_redditUIState)
        }
    }


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

    private fun endJob() {
        val jobToClear = subredditJob.value
        jobToClear?.cancel()
    }

    companion object {
        const val KEY_SUBREDDIT_NEWS = "subreddit_news"
        const val KEY_SUBREDDIT_POST = "subreddit_post"
        const val KEY_SUBREDDIT_SEARCH = "subreddit_search"
        const val DEFAULT_SUBREDDIT = "androiddev"
    }
}