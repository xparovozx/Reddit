package com.example.reddit.ui.fragments.redditor

import android.content.DialogInterface
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
import com.example.reddit.R
import com.example.reddit.databinding.FragmentRedditorBinding
import com.example.reddit.ui.fragments.main_fragment.adapter.SubredditAdapter
import com.example.reddit.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlin.math.max
import kotlin.math.min

@AndroidEntryPoint
class RedditorFragment : Fragment(R.layout.fragment_redditor) {
    private val binding by viewBinding(FragmentRedditorBinding::bind)
    private val viewModel: RedditorViewModel by viewModels()
    private val args: RedditorFragmentArgs by navArgs()
    private var subsAdapter: SubredditAdapter by autoCleared()

    @OptIn(ExperimentalPagingApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRedditorInfo()
        initSubsOfRedditorList()
        bindFlowData(flowData = viewModel.redditorSubs, adapter = subsAdapter)
        setBottomBarMotion()
        binding.subscribeBtn.setOnClickListener {
            viewModel.makeFriend(args.redditorName)
            viewModel.checkFriendshipRelation(args.redditorName)
        }
        binding.unSubscribeBtn.setOnClickListener {
            val positiveListener = DialogInterface.OnClickListener { _, _ ->
                viewModel.unFriend(args.redditorName)
                viewModel.checkFriendshipRelation(args.redditorName)
            }
            showAlertDialog(
                positiveListener = positiveListener,
                message = R.string.alert_dialog_message,
                positiveBtn = R.string.alert_dialog_positive,
                negativeBtn = R.string.alert_dialog_negative
            )
        }
        binding.swipeRefresh.initSwipeToRefresh(adapter = subsAdapter)
        with(viewModel) {
            checkFriendshipRelation(args.redditorName)
            isFriendLiveData.observe(viewLifecycleOwner, ::setSubscribeBtn)
            deleteResultMessage.observe(viewLifecycleOwner) { deleteMessage ->
                requireActivity().makeSnackbarMessage(getString(deleteMessage))
            }
            addFriendResultMessage.observe(viewLifecycleOwner) { addFriendMessage ->
                requireActivity().makeSnackbarMessage(getString(addFriendMessage))
            }
            postMovedSuccess.observe(viewLifecycleOwner) { postMoveSuccessMessage->
                requireActivity().makeSnackbarMessage(getString(postMoveSuccessMessage))
            }
            postMoveError.observe(viewLifecycleOwner) { postMoveErrorMessqage->
                requireActivity().makeSnackbarMessage(getString(postMoveErrorMessqage))
            }
        }
    }
    private fun initRedditorInfo() {
        binding.toolbar.title = args.redditorName
        with(viewModel) {
            getRedditor(args.redditorName)
            getCommentsQuantity(redditorName = args.redditorName)
            redditorLiveData.observe(viewLifecycleOwner) { redditor ->
                with(binding) {
                    val avatarLink = redditor.subreddit.avatarImage.avatarConvert()
                    authorReddit.text = redditor.subreddit.namePrefixed
                    Glide.with(iconUser)
                        .load(avatarLink)
                        .error(R.drawable.ic_redditor_default)
                        .centerInside()
                        .circleCrop()
                        .into(iconUser)
                }
            }
            redditorCommentsQty.observe(viewLifecycleOwner) { commentsQty->
                binding.commentsQty.text = commentsQty.toString()
            }
        }
    }

    private fun initSubsOfRedditorList() {
        subsAdapter = SubredditAdapter(viewModel::subOrUnsubscribe
            , onOpenCommentsBtnClicked = { post->
                val navOptions = navigateWithAnimation()
                val postName = post.name.drop(3)
                val action = RedditorFragmentDirections.actionRedditorFragmentToCommentsFragment( postName, post)
                findNavController().navigate(action, navOptions)
            }, onAuthorNameClicked = {})
        with(subsAdapter) {
            bindItemAdapter(
                itemsAdapter = this,
                viewList = binding.subredditsList
            )
            setAdapterLoadingState(
                viewList = binding.subredditsList,
                progressView = binding.progress,
                noItemsView = binding.noCommentsAvailable
            )
            scrollToTopOnUpdate(binding.subredditsList)
        }
    }

    private fun setSubscribeBtn(isFriend: Boolean) {
        binding.subscribeBtn.isVisible = !isFriend
        binding.unSubscribeBtn.isVisible = isFriend
    }

    private fun setBottomBarMotion() {
        binding.subredditsList.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            val bottomMenu = requireActivity().findViewById<View>(R.id.bottomMenu)
            val deltaY = scrollY - oldScrollY
            when {
                deltaY > 0 && bottomMenu.isShown -> {
                    bottomMenu.translationY = max(
                        0f,
                        min(
                            bottomMenu.height.toFloat(),
                            bottomMenu.translationY + deltaY
                        )
                    )
                    return@setOnScrollChangeListener
                }
                bottomMenu.translationY <= 0 -> {
                    bottomMenu.translationY = 0f
                    return@setOnScrollChangeListener
                }
                deltaY < 0 -> {
                    bottomMenu.translationY = min(
                        bottomMenu.height.toFloat(),
                        min(
                            bottomMenu.height.toFloat(),
                            bottomMenu.translationY + deltaY
                        )
                    )
                    return@setOnScrollChangeListener
                }
            }
        }
    }
}