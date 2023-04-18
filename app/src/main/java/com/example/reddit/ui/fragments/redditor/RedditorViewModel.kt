package com.example.reddit.ui.fragments.redditor

import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.reddit.R
import com.example.reddit.data.MainRepository
import com.example.reddit.data.RedditItem
import com.example.reddit.data.RedditorRepository
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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RedditorViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val redditorRepo: RedditorRepository,
    private val subsRepo: MainRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    init {
        if (!savedStateHandle.contains(KEY_REDDITORSUB)) {
            savedStateHandle.set(KEY_REDDITORSUB, DEFAULT_SUBREDDIT)
        }
    }

    private val clearListChannel = Channel<Unit>(Channel.CONFLATED)
    private val _redditorLiveData = MutableLiveData<RedditItem.Redditor>()
    val redditorLiveData: LiveData<RedditItem.Redditor>
        get() = _redditorLiveData
    private val _isFriendLiveData = MutableLiveData<Boolean>()
    val isFriendLiveData: LiveData<Boolean>
        get() = _isFriendLiveData
    private val _redditorCommentsQty = MutableLiveData<Int>()
    val redditorCommentsQty: LiveData<Int>
        get() = _redditorCommentsQty
    val deleteResultMessage = SingleLiveEvent<Int>()
    val addFriendResultMessage = SingleLiveEvent<Int>()
    val postMovedSuccess = SingleLiveEvent<Int>()
    val postMoveError = SingleLiveEvent<Int>()

    fun checkFriendshipRelation(redditorName: String) {
        viewModelScope.launch {
            try {
                val isFriend = redditorRepo.getFriendRelation(redditorName)
                _isFriendLiveData.postValue(isFriend)
            } catch (t: Throwable) {
                Timber.tag("FriendRelationsError").d("Friends relations error = $t")
            }
        }
    }

    fun getRedditor(redditorName: String) {
        viewModelScope.launch {
            try {
                val redditor = redditorRepo.getRedditor(redditorName)
                _redditorLiveData.postValue(redditor)
            } catch (t: Throwable) {
                Timber.tag("Redditor").d("Redditor error = $t")
            }
        }
    }

    fun makeFriend(redditor: String) {
        viewModelScope.launch {
            try {
                redditorRepo.makeFriend(redditor)
                addFriendResultMessage.postValue(R.string.snackbar_friend_add)
            } catch (t: Throwable) {
                addFriendResultMessage.postValue(R.string.snackbar_friend_unsuccess_add)
                Timber.tag("MakeFriendError").d("Make Friend Error = $t")
            }
        }
    }

    fun unFriend(redditorName: String) {
        viewModelScope.launch {
            try {
                redditorRepo.unFriend(redditorName)
                deleteResultMessage.postValue(
                    R.string.snackbar_friend_removed
                )
            } catch (e: NullPointerException) {
                Timber.tag("MakeFriendGetNullResponse").d("Unmake Friend Error = $e")
                deleteResultMessage.postValue(
                    R.string.snackbar_friend_removed
                )
            } catch (t: Throwable) {
                deleteResultMessage.postValue(
                    R.string.snackbar_friend_unsuccess_remove
                )
            }
        }
    }

    @ExperimentalPagingApi
    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun getRedditorSubs(redditor: String): Flow<PagingData<RedditItem.RedditPost>> {
        return flowOf(
            clearListChannel.receiveAsFlow().map {
                PagingData.empty<RedditItem.RedditPost>()
            },
            savedStateHandle.getLiveData<String>(KEY_REDDITORSUB)
                .asFlow()
                .flatMapLatest {
                    redditorRepo.getRedditorSubs(
                        redditorName = redditor,
                        pageSize = DEFAULT_PAGE_SIZE
                    )
                }.cachedIn(viewModelScope)
                .flowOn(Dispatchers.IO)
        ).flattenMerge(DEFAULT_CONCURRENCY)
    }

    @ExperimentalPagingApi
    @ExperimentalCoroutinesApi
    @FlowPreview
    val redditorSubs = getRedditorSubs(
        redditor = savedStateHandle.get<String>(REDDITOR_NAME_KEY) ?: ""
    )

    fun getCommentsQuantity(redditorName: String) {
        viewModelScope.launch {
            try {
                val subsQty = userRepo.getUserCommentsSize(redditorName)
                _redditorCommentsQty.postValue(subsQty)
            } catch (t: Throwable) {
                Timber.tag("GetUserIdentityError").d("User Subs error = $t")
            }
        }
    }

    fun subOrUnsubscribe(subreddit: RedditItem.RedditPost) {
        viewModelScope.launch {
            when (subreddit.isSubscribed) {
                true -> {
                    try {
                        subsRepo.unSaveSubreddit(subreddit = subreddit)
                        Timber.tag("Subscribe").d("unssubscribed = ${subreddit.subredditId}")
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

    companion object {
        const val KEY_REDDITORSUB = "redditor_sub"
        const val DEFAULT_SUBREDDIT = "androiddev"
        const val REDDITOR_NAME_KEY = "redditorName"
    }
}