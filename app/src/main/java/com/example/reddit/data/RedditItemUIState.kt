package com.example.reddit.data

import androidx.paging.PagingData

sealed class RedditItemUIState{
    data class Success<T : RedditItem>(val itemPagingData: PagingData<T>?) : RedditItemUIState()
    data class Error(val exception: Throwable) : RedditItemUIState()
}
