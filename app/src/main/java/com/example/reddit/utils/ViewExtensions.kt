package com.example.reddit.utils

import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.navigation.NavOptions
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.google.android.material.textfield.TextInputEditText
import com.example.reddit.R
import com.example.reddit.data.RedditItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

/**
 * Load [uri] in the [ImageView].
 */
fun ImageView.load(uri: String?) {
    Glide.with(context)
        .load(uri)
        .transition(withCrossFade())
        .into(this)
}

fun navigateWithAnimation(): NavOptions {
    return NavOptions.Builder()
        .setLaunchSingleTop(true)
        .setEnterAnim(R.anim.enter_anim)
        .setExitAnim(R.anim.exit_anim)
        .setPopEnterAnim(R.anim.pop_enter_anim)
        .setPopExitAnim(R.anim.pop_exit_anim)
        .build()
}

fun RadioGroup.changeTextColor(
    context: Context,
    darkMode: Int,
    @ColorRes checkedColor: Int,
    @ColorRes unCheckedColor: Int,
    @ColorRes uncheckedColorDarkMode: Int
) {

    this.forEach {
        (it as RadioButton).apply {
            Timber.tag("DarkMode").d("mode = $darkMode")
            when {
                isChecked.not() && darkMode == AppCompatDelegate.MODE_NIGHT_NO -> {
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            unCheckedColor
                        )
                    )
                    this.typeface = Typeface.DEFAULT
                }
                isChecked.not() && darkMode == AppCompatDelegate.MODE_NIGHT_YES -> {
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            uncheckedColorDarkMode
                        )
                    )
                    this.typeface = Typeface.DEFAULT
                }
                else -> {
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            checkedColor
                        )
                    )
                    this.typeface = Typeface.DEFAULT_BOLD
                }
            }
        }
    }
}

fun <I : RedditItem> ViewGroup.checkHiddenViewVisibility(item: I) {
    this.visibility = when (item) {
        is RedditItem.RedditPost -> if (item.isExpandable) View.VISIBLE else View.GONE
        is RedditItem.RedditComment -> if (item.isExpandable) View.VISIBLE else View.GONE
        else -> View.GONE
    }
}

fun <T : RedditItem, VH : RecyclerView.ViewHolder> SwipeRefreshLayout.initSwipeToRefresh(
    adapter: PagingDataAdapter<T, VH>
) {
    this.setOnRefreshListener {
        isRefreshing = false
        adapter.refresh()
    }
}

@ExperimentalCoroutinesApi
fun TextInputEditText.textChangedFlow(): StateFlow<String> {
    val query = MutableStateFlow("")
    val watcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            query.value = s.toString()
        }
        override fun afterTextChanged(s: Editable?) {}
    }
    addTextChangedListener(watcher)
    return query
}

fun Toolbar.initToolbar(idArticle: String?) {
    this.title = idArticle
    setNavigationIcon(R.drawable.ic_return)
}








