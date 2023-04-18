package com.example.reddit.ui.fragments.comments.more_comments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.RecyclerView
import com.example.reddit.R
import com.example.reddit.databinding.FragmentMoreCommentsBinding
import com.example.reddit.ui.fragments.comments.CommentsFragmentDirections
import com.example.reddit.ui.fragments.comments.adapter.CommentPageAdapter
import com.example.reddit.utils.*
import com.example.reddit.utils.Constants.DEFAULT_PAGE_SIZE
import com.example.reddit.utils.Constants.DIRECTION_DOWN
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalPagingApi
@AndroidEntryPoint
class MoreCommentsFragment : Fragment(R.layout.fragment_more_comments) {

    private val binding by viewBinding(FragmentMoreCommentsBinding::bind)
    private val viewModel: MoreCommentsViewModel by viewModels()
    private var commentsAdapter: CommentPageAdapter by autoCleared()
    private val args: MoreCommentsFragmentArgs by navArgs()

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCommentList()
        binding.appBar.toolbar.initToolbar(args.subTitle)
        bindUIData(
            stateFlow = viewModel.commentUIState,
            adapter = commentsAdapter
        )
        viewModel.commentMovedSuccess.observe(viewLifecycleOwner) { commentAddedMessage ->
            requireActivity().makeSnackbarMessage(getString(commentAddedMessage))
        }
        viewModel.commentMoveError.observe(viewLifecycleOwner) { commentAddError ->
            requireActivity().makeSnackbarMessage(getString(commentAddError))
        }
        binding.appBar.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    private fun initCommentList() {
        var pageSize = DEFAULT_PAGE_SIZE

        commentsAdapter = CommentPageAdapter({ comment ->
            goToRedditorFragment(comment.author ?: "")
        }, { comment ->
            viewModel.saveOrUnsaveComment(comment)
        })
        bindItemAdapter(
            itemsAdapter = commentsAdapter,
            viewList = binding.commentsList,
        )
        commentsAdapter.setAdapterLoadingState(
            viewList = binding.commentsList,
            progressView = binding.progressComment,
            noItemsView = binding.noCommentsAvailable
        )
        binding.commentsList.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(DIRECTION_DOWN) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        pageSize += DEFAULT_PAGE_SIZE
                        viewModel.getComments(article = args.subredditId, pageSize = pageSize)
                    } else {
                    }
                }
            }
        )
        commentsAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                when (positionStart) {
                    0 -> binding.commentsList.scrollToPosition(0)
                    else -> binding.commentsList.scrollToPosition(pageSize - DEFAULT_PAGE_SIZE + 1)
                }
            }
        })
    }

    private fun goToRedditorFragment(author: String) {
        val navOptions = navigateWithAnimation()
        val action =
            CommentsFragmentDirections.actionCommentsFragmentToRedditorFragment(redditorName = author)
        findNavController().navigate(action, navOptions)
    }
}