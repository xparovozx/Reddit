package com.example.reddit.ui.fragments.comments.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.example.reddit.data.RedditItem
import com.example.reddit.databinding.ItemCommentBinding

class CommentPageAdapter(
    private val onAuthorNameClicked: (comment: RedditItem.RedditComment) -> Unit,
    private val onSaveCommentBtnClicked: (comment: RedditItem.RedditComment) -> Unit
) : PagingDataAdapter<RedditItem.RedditComment, CommentViewHolder>(CommentDiffItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val itemCommentBinding =
            ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(
            onAuthorNameClicked = onAuthorNameClicked,
            onSaveCommentBtnClicked = onSaveCommentBtnClicked,
            itemCommentBinding = itemCommentBinding
        )
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}







