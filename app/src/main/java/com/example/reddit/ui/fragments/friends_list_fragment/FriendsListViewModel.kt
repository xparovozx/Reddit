package com.example.reddit.ui.fragments.friends_list_fragment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.reddit.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FriendsListViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    init {
        if (!savedStateHandle.contains(KEY_FRIEND_REDDITOR)) {
            savedStateHandle.set(KEY_FRIEND_REDDITOR, DEFAULT_REDDIT)
        }
    }

    companion object {
        const val KEY_FRIEND_REDDITOR = "friend_redditor"
        const val DEFAULT_REDDIT = "androiddev"
    }
    private val clearListChannel = Channel<Unit>(Channel.CONFLATED)

    @OptIn(ExperimentalPagingApi::class)
    @FlowPreview
    @ExperimentalCoroutinesApi
    val friends = flowOf(
        clearListChannel.receiveAsFlow().map{ PagingData.empty<RedditItem.Redditor>()},
        savedStateHandle.getLiveData<String>(FriendsListViewModel.KEY_FRIEND_REDDITOR)
            .asFlow()
            .flatMapLatest {
                userRepo.getUsersFriendsList(pageSize = 10)
            }
            .cachedIn(viewModelScope)
            .flowOn(Dispatchers.IO)
    ).flattenMerge(2)
}