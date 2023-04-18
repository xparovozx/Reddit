package com.example.reddit.ui.fragments.comments.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.reddit.data.RedditItem.*

object CommentDiffItemCallback : DiffUtil.ItemCallback<RedditComment>() {
    override fun areItemsTheSame(oldItem: RedditComment, newItem: RedditComment): Boolean {
        return oldItem.name == newItem.name
    }
    override fun areContentsTheSame(oldItem: RedditComment, newItem: RedditComment): Boolean {
        return oldItem == newItem
    }
}