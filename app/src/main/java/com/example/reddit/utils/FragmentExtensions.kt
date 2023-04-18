package com.example.reddit.utils

import android.content.DialogInterface
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.reddit.R
import com.example.reddit.data.RedditItem
import com.example.reddit.data.RedditItemUIState
import com.example.reddit.ui.fragments.friends_list_fragment.adapter.FriendsPageAdapter
import com.example.reddit.ui.fragments.main_fragment.adapter.RedditLoaderStateAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

fun Fragment.toast(@StringRes stringRes: Int) {
    Toast.makeText(requireContext(), stringRes, Toast.LENGTH_SHORT).show()
}

fun Fragment.showAlertDialog(
    positiveListener: DialogInterface.OnClickListener,
    message: Int,
    positiveBtn: Int,
    negativeBtn: Int
) {
    val negativeListener = DialogInterface.OnClickListener { dialog, _ ->
        dialog.dismiss()
    }
    val alertDialog =
        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert)
            .setTitle(R.string.alert_dialog_title)
            .setMessage(message)
            .setPositiveButton(positiveBtn, positiveListener)
            .setNegativeButton(negativeBtn, negativeListener)
    alertDialog.show()
}

fun <T : RedditItem, VH : RecyclerView.ViewHolder> Fragment.bindItemAdapter(
    itemsAdapter: PagingDataAdapter<T, VH>,
    viewList: RecyclerView,
) {
    with(viewList) {
        adapter = itemsAdapter
        adapter = itemsAdapter.withLoadStateHeaderAndFooter(
            header = RedditLoaderStateAdapter(),
            footer = RedditLoaderStateAdapter()
        )
        setHasFixedSize(true)
        layoutManager =
            if (itemsAdapter is FriendsPageAdapter) GridLayoutManager(requireContext(), 2)
            else LinearLayoutManager(requireContext())
        itemsAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }
}

fun <T : RedditItem, VH : RecyclerView.ViewHolder>PagingDataAdapter<T,VH>.setAdapterLoadingState(
    viewList: RecyclerView,
    progressView: ProgressBar,
    noItemsView: TextView,
    fabView: FloatingActionButton? = null,
    showAllCommentsBtn: ImageView? = null
) {
    this.addLoadStateListener { state ->
        viewList.isVisible = state.refresh != LoadState.Loading && (this.itemCount != 0)
        progressView.isVisible = state.refresh == LoadState.Loading
        noItemsView.isVisible =
            (state.refresh != LoadState.Loading) && (this.itemCount < 1)
        fabView?.isVisible = state.refresh != LoadState.Loading && this.itemCount != 0
        showAllCommentsBtn?.isVisible =
            state.refresh != LoadState.Loading && this.itemCount != 0
    }
}

fun <T : RedditItem, VH : RecyclerView.ViewHolder> PagingDataAdapter<T, VH>.scrollToTopOnUpdate(
    viewList: RecyclerView,
) {
    this.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            if (positionStart == 0) viewList.scrollToPosition(0)
        }
    })
}

fun <T : RedditItem, VH : RecyclerView.ViewHolder> Fragment.bindFlowData(
    flowData: Flow<PagingData<T>>,
    adapter: PagingDataAdapter<T, VH>,
): Job {
    return viewLifecycleOwner.lifecycleScope.launchWhenCreated {
        flowData.collectLatest {
            adapter.submitData(it)
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <S : RedditItemUIState, I : RedditItem, VH : RecyclerView.ViewHolder> Fragment. bindUIData(
    stateFlow: MutableStateFlow<S>,
    adapter: PagingDataAdapter<I, VH>
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            stateFlow.collectLatest { itemUIState ->
                when (itemUIState) {
                    is RedditItemUIState.Success<*> -> {
                        adapter.submitData(itemUIState.itemPagingData as PagingData<I>)
                    }
                    is RedditItemUIState.Error -> {}
                }
            }
        }
    }
}

suspend fun <T : RedditItem> Flow<PagingData<T>>.collectItems(stateItems: MutableStateFlow<RedditItemUIState>) {
    this.collectLatest {
        stateItems.value = RedditItemUIState.Success(it)
    }
}

fun Fragment.showExitDialog() {
    val negativeListener = DialogInterface.OnClickListener { dialog, _ ->
        dialog.dismiss()
    }
    val positiveListener = DialogInterface.OnClickListener { _, _ ->
        requireActivity().finishAndRemoveTask()
    }
    val alertDialog =
        MaterialAlertDialogBuilder(
            requireContext(),
            R.style.ThemeOverlay_AppCompat_Dialog_Alert
        )
            .setTitle(R.string.alert_dialog_title)
            .setMessage(R.string.snackbar_exit_rationale)
            .setPositiveButton(R.string.snackbar_action_exit, positiveListener)
            .setNegativeButton(R.string.snackbar_action_noexit, negativeListener)
    alertDialog.show()
}














