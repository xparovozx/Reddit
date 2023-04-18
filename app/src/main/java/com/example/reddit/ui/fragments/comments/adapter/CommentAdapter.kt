package com.example.reddit.ui.fragments.comments.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.example.reddit.data.RedditItem

class CommentAdapter(
    private val onAuthorNameClicked: (comment: RedditItem.RedditComment) -> Unit,
    private val onSaveCommentBtnClicked: (comment: RedditItem.RedditComment) -> Unit
) : AsyncListDifferDelegationAdapter<RedditItem.RedditComment>(CommentDiffItemCallback) {
    init {
        delegatesManager.addDelegate(RedditCommentDelegate(
            onAuthorNameClicked,
            onSaveCommentBtnClicked
        ))
    }
}