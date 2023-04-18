package com.example.reddit.ui.fragments.main_fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.example.reddit.data.RedditItem
import com.example.reddit.databinding.ItemSubsBinding
import com.example.reddit.utils.checkHiddenViewVisibility

class SubredditAdapter(
    private val onSubscribeBtnClicked: (post: RedditItem.RedditPost) -> Unit,
    private val onOpenCommentsBtnClicked: (post: RedditItem.RedditPost) -> Unit,
    private val onAuthorNameClicked: (redditorName: String) -> Unit
) : PagingDataAdapter<RedditItem.RedditPost, RedditViewHolder>(RedditDiffItemCallback) {

    override fun onBindViewHolder(holder: RedditViewHolder, position: Int) {
        getItem(position)?.let { itemSub ->
            with(holder) {
                bind(itemSub)
                hiddenLayout.checkHiddenViewVisibility(itemSub)
                cardView.setOnClickListener {
                    itemSub.isExpandable = !itemSub.isExpandable
                    hiddenLayout.checkHiddenViewVisibility(itemSub)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RedditViewHolder {
        val itemSubsBinding =
            ItemSubsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RedditViewHolder(
            onSubscribeBtnClicked = onSubscribeBtnClicked,
            onOpenCommentsBtnClicked = onOpenCommentsBtnClicked,
            onAuthorNameClicked = onAuthorNameClicked,
            itemSubsBinding = itemSubsBinding,
            )
    }
}



