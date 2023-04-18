package com.example.reddit.ui.fragments.friends_list_fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.reddit.R
import com.example.reddit.data.RedditItem
import com.example.reddit.databinding.FragmentFriendsListBinding
import com.example.reddit.ui.fragments.friends_list_fragment.adapter.FriendsPageAdapter
import com.example.reddit.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview

@AndroidEntryPoint
class FriendsListFragment : Fragment(R.layout.fragment_friends_list) {
    private val binding by viewBinding(FragmentFriendsListBinding::bind)
    private val viewModel: FriendsListViewModel by viewModels()
    private var friendsAdapter: FriendsPageAdapter by autoCleared()

    @OptIn(FlowPreview::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initToolbar()
        bindFlowData(flowData = viewModel.friends, adapter = friendsAdapter)
        binding.swipeRefresh.initSwipeToRefresh(adapter = friendsAdapter)
        binding.appBar.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initList() {
        friendsAdapter = FriendsPageAdapter { view: View, redditor: RedditItem.Redditor ->
            val navOptions = navigateWithAnimation()
            val action = FriendsListFragmentDirections.actionFriendsListFragmentToRedditorFragment(
                redditorName = redditor.name
            )
            findNavController().navigate(action, navOptions)
        }
        bindItemAdapter(
            itemsAdapter = friendsAdapter,
            viewList = binding.friendsList,
        )
        friendsAdapter.setAdapterLoadingState(
            viewList = binding.friendsList,
            progressView = binding.progress,
            noItemsView = binding.noFriendsAvailable
        )
        friendsAdapter.scrollToTopOnUpdate(binding.friendsList)
    }

    private fun initToolbar() {
        with(binding.appBar.toolbar) {
            title = resources.getString(R.string.friends_list_title)
            titleMarginStart = 156
            this.setNavigationIcon(R.drawable.ic_return)
        }
    }
}