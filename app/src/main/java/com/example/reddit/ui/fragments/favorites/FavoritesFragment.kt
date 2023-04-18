package com.example.reddit.ui.fragments.favorites

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.reddit.R
import com.example.reddit.data.RedditItem
import com.example.reddit.databinding.FragmentFavoritesBinding
import com.example.reddit.di.DarkLightPrefs
import com.example.reddit.ui.fragments.comments.adapter.CommentPageAdapter
import com.example.reddit.ui.fragments.main_fragment.adapter.SubredditAdapter
import com.example.reddit.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_favorites),
    RadioGroup.OnCheckedChangeListener {
    private val binding by viewBinding(FragmentFavoritesBinding::bind)
    private val viewModel: FavoritesViewModel by viewModels()
    private var commentsAdapter: CommentPageAdapter by autoCleared()
    private var postsAdapter: SubredditAdapter by autoCleared()

    @Inject
    lateinit var darkPrefs: DarkLightPrefs

    @OptIn(ExperimentalPagingApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navOptions = navigateWithAnimation()
        with(binding) {
            toggleContentGroup.setOnCheckedChangeListener(this@FavoritesFragment)
            toggleTypeGroup.setOnCheckedChangeListener(this@FavoritesFragment)
            allContentBtn.typeface = Typeface.DEFAULT_BOLD
            subredditBtn.typeface = Typeface.DEFAULT_BOLD
        }
        postsAdapter = SubredditAdapter(viewModel::subOrUnsubscribe,
            { redditPost ->
                goToCommentsFragment(redditPost, navigateWithAnimation())
            }, { redditorName ->
                goToRedditorFragment(redditorName, navigateWithAnimation())
            }
        )
        commentsAdapter = CommentPageAdapter({
            goToUserProfileFragment(navOptions)
        }, viewModel::saveOrUnsaveComment)
        setupAdapter(postsAdapter)
        bindFlowData(
            flowData = viewModel.allFavoritePosts,
            adapter = postsAdapter
        )
        binding.swipeRefresh.initSwipeToRefresh(adapter = postsAdapter)
        with(viewModel) {
            postMovedSuccess.observe(viewLifecycleOwner) { postMoveSuccessMessage ->
                requireActivity().makeSnackbarMessage(getString(postMoveSuccessMessage))
            }
            postMoveError.observe(viewLifecycleOwner) { postMoveErrorMessqage ->
                requireActivity().makeSnackbarMessage(getString(postMoveErrorMessqage))
            }
            commentMovedSuccess.observe(viewLifecycleOwner) { commentAddedMessage ->
                requireActivity().makeSnackbarMessage(getString(commentAddedMessage))
            }
            commentMoveError.observe(viewLifecycleOwner) { commentAddError ->
                requireActivity().makeSnackbarMessage(getString(commentAddError))
            }
        }
    }


    @OptIn(ExperimentalPagingApi::class)
    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        with(binding) {
            val subSelectedPosition =
                (subredditBtn.isChecked) && (selectedBtn.isChecked)
            val subAllPosition =
                (subredditBtn.isChecked) && (allContentBtn.isChecked)
            val commentSelectedPosition =
                (commentBtn.isChecked) && (selectedBtn.isChecked)
            val userCommentsPosition =
                (commentBtn.isChecked) && (allContentBtn.isChecked)
            group?.changeTextColor(
                context = requireContext(),
                darkMode = darkPrefs.getDarkThemeStatus(),
                checkedColor = R.color.white,
                unCheckedColor = R.color.black,
                uncheckedColorDarkMode = R.color.white
            )

            when {
                subSelectedPosition -> {
                    setupAdapter(postsAdapter)
                    bindFlowData(
                        flowData = viewModel.postSelected,
                        adapter = postsAdapter
                    )
                }

                subAllPosition -> {
                    setupAdapter(postsAdapter)
                    bindFlowData(
                        flowData = viewModel.allFavoritePosts,
                        adapter = postsAdapter
                    )
                }

                userCommentsPosition -> {
                    setupAdapter(commentsAdapter)
                    bindFlowData(
                        flowData = viewModel.userComments,
                        adapter = commentsAdapter
                    )
                }

                commentSelectedPosition -> {
                    setupAdapter(commentsAdapter)
                    bindFlowData(
                        flowData = viewModel.savedComments,
                        adapter = commentsAdapter
                    )
                }
                else -> {}
            }
        }
    }

    private fun goToCommentsFragment(redditPost: RedditItem.RedditPost, navOptions: NavOptions) {
        val action =
            FavoritesFragmentDirections.actionFavoritesFragmentToCommentsFragment(
                redditPost.name.drop(3),
                redditPost
            )
        findNavController().navigate(action, navOptions)
    }

    private fun goToRedditorFragment(redditorName: String, navOptions: NavOptions) {
        val action =
            FavoritesFragmentDirections.actionFavoritesFragmentToRedditorFragment(redditorName)
        findNavController().navigate(action, navOptions)
    }

    private fun goToUserProfileFragment(navOptions: NavOptions) {
        val action =
            FavoritesFragmentDirections.actionFavoritesFragmentToUserProfileFragment()
        findNavController().navigate(action, navOptions)
    }

    private fun <T : RedditItem, VH : RecyclerView.ViewHolder> setupAdapter(adapter: PagingDataAdapter<T, VH>) {
        bindItemAdapter(
            itemsAdapter = adapter,
            viewList = binding.contentList,
        )
        adapter.setAdapterLoadingState(
            viewList = binding.contentList,
            progressView = binding.progress,
            noItemsView = binding.noPostsAvailable
        )
        adapter.scrollToTopOnUpdate(binding.contentList)
    }
}