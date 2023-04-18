package com.example.reddit.ui.fragments.comments.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.example.reddit.data.RedditItem
import com.example.reddit.databinding.ItemCommentBinding

class RedditCommentDelegate(
    private val onAuthorNameClicked: (comment: RedditItem.RedditComment) -> Unit,
    private val onSaveCommentBtnClicked: (comment: RedditItem.RedditComment) -> Unit
) : AbsListItemAdapterDelegate<RedditItem.RedditComment, RedditItem.RedditComment, CommentViewHolder>() {
    override fun isForViewType(
        item: RedditItem.RedditComment,
        items: MutableList<RedditItem.RedditComment>,
        position: Int
    ): Boolean {
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup): CommentViewHolder {
        val itemCommentBinding =
            ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(
            onAuthorNameClicked = onAuthorNameClicked,
            onSaveCommentBtnClicked = onSaveCommentBtnClicked,
            itemCommentBinding = itemCommentBinding
        )
    }

    override fun onBindViewHolder(
        item: RedditItem.RedditComment,
        holder: CommentViewHolder,
        payloads: MutableList<Any>
    ) {
        holder.bind(item)
    }
}