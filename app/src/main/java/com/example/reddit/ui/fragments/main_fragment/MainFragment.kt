package com.example.reddit.ui.fragments.main_fragment

import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.reddit.R
import com.example.reddit.databinding.FragmentMainBinding
import com.example.reddit.di.DarkLightPrefs
import com.example.reddit.ui.fragments.main_fragment.adapter.SubredditAdapter
import com.example.reddit.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main), RadioGroup.OnCheckedChangeListener
{
    private val binding by viewBinding(FragmentMainBinding::bind)

    @ExperimentalCoroutinesApi
    @FlowPreview
    @ExperimentalPagingApi
    private val viewModel: MainViewModel by viewModels()
    private var subsAdapter: SubredditAdapter by autoCleared()

    @Inject
    lateinit var darkPrefs: DarkLightPrefs

    @OptIn(ExperimentalPagingApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toggleContentGroup.setOnCheckedChangeListener(this@MainFragment)
        initList()
        handleOnBackPress()
        bindUIData(
            stateFlow = viewModel.redditUIState,
            adapter = subsAdapter
        )
        val textChangedFlow = binding.searchInput.textChangedFlow().filterNotNull()
        binding.searchInput.setOnEditorActionListener { _, _, _ ->
            viewModel.getPostsSearch(textChangedFlow)
            true
        }
        viewModel.postMovedSuccess.observe(viewLifecycleOwner) { postMoveSuccessMessage ->
            requireActivity().makeSnackbarMessage(getString(postMoveSuccessMessage))
        }
        viewModel.postMoveError.observe(viewLifecycleOwner) { postMoveErrorMessqage ->
            requireActivity().makeSnackbarMessage(getString(postMoveErrorMessqage))
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        with(binding) {
            val newsSelected = getNewsBtn.isChecked
            val topSelected = getPopularBtn.isChecked
            group?.changeTextColor(
                context = requireContext(),
                darkMode = darkPrefs.getDarkThemeStatus(),
                checkedColor = R.color.design_default_color_primary,
                unCheckedColor = R.color.black,
                uncheckedColorDarkMode = R.color.white
            )
            when {
                newsSelected -> {
                    viewModel.getPostsNews()
                }
                topSelected -> {
                    viewModel.getTopNews()
                }
                else -> {}
            }
        }
    }


    @ExperimentalPagingApi
    private fun initList() {
        initSubAdapter()
        bindItemAdapter(
            itemsAdapter = subsAdapter,
            viewList = binding.subredditsList,
        )
        subsAdapter.setAdapterLoadingState(
            viewList = binding.subredditsList,
            progressView = binding.progress,
            noItemsView = binding.noPostsAvailable
        )
        subsAdapter.scrollToTopOnUpdate(binding.subredditsList)
        binding.swipeRefresh.initSwipeToRefresh(adapter = subsAdapter)
    }


    @ExperimentalPagingApi
    private fun initSubAdapter() {
        val navOptions = navigateWithAnimation()
        subsAdapter = SubredditAdapter(
            viewModel::subOrUnsubscribe,
            { redditPost ->
                val action = MainFragmentDirections.actionMainFragmentToCommentsFragment(
                    redditPost.name.drop(3), redditPost
                )
                findNavController().navigate(action, navOptions)
            },
            {
                val action =
                    MainFragmentDirections.actionMainFragmentToRedditorFragment(redditorName = it)
                findNavController().navigate(action, navOptions)
            },
        )
    }

    private fun handleOnBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitDialog()
                }
            })
    }
}