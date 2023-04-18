package com.example.reddit.ui.fragments.comments.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.reddit.R
import com.example.reddit.data.RedditItem
import com.example.reddit.databinding.ItemCommentBinding
import com.example.reddit.utils.getTimeAgo

class CommentViewHolder(
    val itemCommentBinding: ItemCommentBinding,
    onAuthorNameClicked: (comment: RedditItem.RedditComment) -> Unit,
    onSaveCommentBtnClicked: (comment: RedditItem.RedditComment) -> Unit
) : RecyclerView.ViewHolder(itemCommentBinding.root) {

    private var comment: RedditItem.RedditComment? = null
    init {
        with(itemCommentBinding) {
            commentatorName.setOnClickListener {
                comment?.let(onAuthorNameClicked)
            }
            downloadBtn.setOnClickListener {
                if (comment?.isSubscribed == true) {
                    downloadBtn.setImageResource(R.drawable.ic_download)
                    downloadText.text =
                        downloadText.context.resources.getString(R.string.download_txt)
                    downloadText.setTextColor(downloadText.context.getColor(R.color.reddit_text_color_default))
                } else {
                    downloadBtn.setImageResource(R.drawable.ic_downloaded_comment)
                    downloadText.text = downloadText.context.getString(R.string.downloaded_txt)
                    downloadText.setTextColor(downloadText.context.getColor(R.color.design_default_color_primary))
                }
                comment?.let(onSaveCommentBtnClicked)
            }
        }
    }

    fun bind(commentForSub: RedditItem.RedditComment) {
        comment = commentForSub
        with(itemCommentBinding) {
            commentatorName.text = commentForSub.author
            textOfComment.text = commentForSub.body
            commentedAtTime.text = commentForSub.createdAt?.getTimeAgo() ?: ""
            if (comment?.isSubscribed == true) {
                downloadBtn.setImageResource(R.drawable.ic_downloaded_comment)
                downloadText.text = downloadText.context.getString(R.string.downloaded_txt)
                downloadText.setTextColor(downloadText.context.getColor(R.color.design_default_color_primary))
            } else {
                downloadBtn.setImageResource(R.drawable.ic_download)
                downloadText.text = downloadText.context.resources.getString(R.string.download_txt)
                downloadText.setTextColor(downloadText.context.getColor(R.color.reddit_text_color_default))
            }
        }
    }
}
