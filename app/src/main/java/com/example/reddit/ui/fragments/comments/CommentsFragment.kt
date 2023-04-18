package com.example.reddit.ui.fragments.comments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.ExperimentalPagingApi
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.reddit.R
import com.example.reddit.data.RedditItem
import com.example.reddit.databinding.FragmentCommentsBinding
import com.example.reddit.ui.fragments.comments.adapter.CommentPageAdapter
import com.example.reddit.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalPagingApi
@AndroidEntryPoint
class CommentsFragment : Fragment(R.layout.fragment_comments) {

    private val binding by viewBinding(FragmentCommentsBinding::bind)
    private val viewModel: CommentsViewModel by viewModels()
    private var commentsAdapter: CommentPageAdapter by autoCleared()
    private val args: CommentsFragmentArgs by navArgs()

    @OptIn(FlowPreview::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val subreddit = args.subreddit
        bindOriginalPost(subreddit)
        initCommentList()
        bindUIData(
            stateFlow = viewModel.commentUIState,
            adapter = commentsAdapter
        )
        with(binding) {
            authorName.setOnClickListener {
                val redditor = binding.authorName.text.toString()
                goToRedditorFragment(redditor)
            }
            appBar.toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            showAllCommentsBtn.setOnClickListener {
                goToMoreCommentsFragment()
            }
            showMoreFab.setOnClickListener {
                goToMoreCommentsFragment()
            }
        }
        viewModel.commentMovedSuccess.observe(viewLifecycleOwner) { commentAddedMessage ->
            requireActivity().makeSnackbarMessage(getString(commentAddedMessage))
        }
        viewModel.commentMoveError.observe(viewLifecycleOwner) { commentAddError ->
            requireActivity().makeSnackbarMessage(getString(commentAddError))
        }
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    private fun initCommentList() {
        commentsAdapter = CommentPageAdapter({ comment ->
            goToRedditorFragment(
                comment.author ?: ""
            )
        }, { comment ->
            viewModel.saveOrUnsaveComment(comment)
        })
        bindItemAdapter(
            itemsAdapter = commentsAdapter,
            viewList = binding.commentsList
        )
        commentsAdapter.setAdapterLoadingState(
            viewList = binding.commentsList,
            progressView = binding.progressComment,
            noItemsView = binding.noCommentsAvailable,
            showAllCommentsBtn = binding.showAllCommentsBtn,
            fabView = binding.showMoreFab
        )
        binding.swipeRefreshComment.initSwipeToRefresh(adapter = commentsAdapter)
    }

    private fun bindOriginalPost(subreddit: RedditItem.RedditPost) {
        with(binding) {
            appBar.toolbar.initToolbar(subreddit.title)
            subredditText.text = subreddit.title
            numberComments.text =
                resources.getString(R.string.comments_number, subreddit.num_comments.toString())
            authorName.text = subreddit.author
            numberLikes.text = subreddit.score.toString()
            subredditText.text = subreddit.subSelfText
            createdAtTime.text = subreddit.createdAt.getTimeAgo()

            val convertedWidth = 320f.convertDpToPixel()
            val convertedHeight = 180f.convertDpToPixel()

            if (((subreddit.url)
                    ?.contains(".jpg") == true || (subreddit.url)
                    ?.contains(".png") == true || (subreddit.url)
                    ?.contains(".gif") == true)
            ) {
                subredditImage.visibility = View.VISIBLE
                linkTextView.visibility = View.GONE
                Glide.with(subredditImage)
                    .load(subreddit.url)
                    .transform(CenterInside(), RoundedCorners(8))
                    .override(convertedWidth.toInt(), convertedHeight.toInt())
                    .into(subredditImage)
            } else {
                subredditImage.visibility = View.GONE
                linkTextView.visibility = View.VISIBLE
                linkTextView.text = subreddit.url
            }
        }
    }

    private fun goToRedditorFragment(redditorName: String) {
        val navOptions = navigateWithAnimation()
        val action =
            CommentsFragmentDirections.actionCommentsFragmentToRedditorFragment(redditorName = redditorName)
        findNavController().navigate(action, navOptions)
    }

    private fun goToMoreCommentsFragment() {
        val navOptions = navigateWithAnimation()
        val action =
            CommentsFragmentDirections.actionCommentsFragmentToMoreCommentsFragment(
                args.idArticle,
                args.subreddit.title ?: ""
            )
        findNavController().navigate(action, navOptions)
    }

    private fun showProgress(isLoading: Boolean, isEmpty: Boolean) {
        with(binding) {
            commentsList.isVisible = !isLoading
            progressComment.isVisible = isLoading
            showAllCommentsBtn.isVisible = !isLoading && !isEmpty
            showMoreFab.isVisible = !isLoading && !isEmpty
        }
    }
}
