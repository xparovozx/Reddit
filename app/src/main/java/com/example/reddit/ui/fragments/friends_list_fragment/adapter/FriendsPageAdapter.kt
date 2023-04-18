package com.example.reddit.ui.fragments.friends_list_fragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.example.reddit.data.RedditItem
import com.example.reddit.databinding.ItemFriendBinding

class FriendsPageAdapter(
    private val onFriendCardClicked: (View, RedditItem.Redditor) -> Unit
) : PagingDataAdapter<RedditItem.Redditor, FriendsViewHolder>(FriendsDiffItemCallback) {

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val itemFriendBinding =
            ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendsViewHolder(
            itemViewBinding = itemFriendBinding,
            onFriendCardClicked = onFriendCardClicked
        )
    }
}