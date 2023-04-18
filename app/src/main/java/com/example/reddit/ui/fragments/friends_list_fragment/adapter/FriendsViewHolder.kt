package com.example.reddit.ui.fragments.friends_list_fragment.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reddit.R
import com.example.reddit.data.RedditItem
import com.example.reddit.databinding.ItemFriendBinding
import com.example.reddit.utils.avatarConvert

class FriendsViewHolder(
    private val itemViewBinding: ItemFriendBinding,
    private val onFriendCardClicked: (View, RedditItem.Redditor) -> Unit
) : RecyclerView.ViewHolder(itemViewBinding.root) {

    fun bind(redditor: RedditItem.Redditor) {
        val cardView = itemViewBinding.itemFriendCard
        cardView.setOnClickListener { card->
            onFriendCardClicked(card, redditor )
        }
        with(itemViewBinding) {
            val friendsIcon = redditor.subreddit.avatarImage.avatarConvert()
            friendName.text = redditor.name
            authorReddit.text = redditor.subreddit.namePrefixed
            Glide.with(iconFriend)
                .load(friendsIcon)
                .error(R.drawable.ic_redditor_default)
                .circleCrop()
                .into(iconFriend)
        }
    }
}