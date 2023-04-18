package com.example.reddit.utils

import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.text.SpannableString
import android.util.DisplayMetrics
import android.view.View
import androidx.paging.PagedList
import androidx.paging.PagingData
import androidx.paging.map
import com.google.android.material.snackbar.Snackbar
import com.example.reddit.R
import com.example.reddit.data.RedditItem
import com.example.reddit.utils.Constants.DAY_MILLIS
import com.example.reddit.utils.Constants.FIRST_ARAB_LETTER
import com.example.reddit.utils.Constants.FIRST_HEBREW_LETTER
import com.example.reddit.utils.Constants.HOUR_MILLIS
import com.example.reddit.utils.Constants.LAST_ARAB_LETTER
import com.example.reddit.utils.Constants.LAST_HEBREW_LETTER
import com.example.reddit.utils.Constants.MINUTE_MILLIS

fun Long.getTimeAgo(): String? {
    var time = this
    val now = System.currentTimeMillis()

    if (time < 1000000000000L) {
        time *= 1000;
    }
    if (time > now || time <= 0) {
        return null
    }
    val diff = now - time

    return when {
        diff < MINUTE_MILLIS -> {
            "недавно"
        }
        diff < 2 * MINUTE_MILLIS -> {
            "минуту назад"
        }
        diff < 5 * MINUTE_MILLIS -> {
            (diff / MINUTE_MILLIS).toString() + " минуты назад"
        }
        diff < 60 * MINUTE_MILLIS -> {
            (diff / MINUTE_MILLIS).toString() + "  минут назад"
        }
        diff < 120 * MINUTE_MILLIS -> {
            "час назад"
        }
        diff < 5 * HOUR_MILLIS -> {
            (diff / HOUR_MILLIS).toString() + " часа назад"
        }

        diff < 24 * HOUR_MILLIS -> {
            (diff / HOUR_MILLIS).toString() + "  часов назад"
        }
        diff < 48 * HOUR_MILLIS -> {
            "вчера"
        }
        diff < 120 * HOUR_MILLIS -> {
            (diff / HOUR_MILLIS).toString() + " дня назад"
        }
        else -> {
            (diff / DAY_MILLIS).toString() + " дней назад"
        }
    }
}

fun String.avatarConvert(): String {
    return when {
        contains(".jpg?") -> substringBefore(".jpg").plus(".jpg")
        contains(".png?") -> substringBefore(".png").plus(".png")
        else -> this
    }
}

fun Float.convertDpToPixel(): Float {
    return this * (Resources.getSystem().displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Float.convertPixelsToDp(): Float {
    return this / (Resources.getSystem().displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun String.isTextFromRightToLeft(): Boolean {
    forEachIndexed { index, _ ->
        val codePoint = codePointAt(index)
        return (codePoint in FIRST_ARAB_LETTER..LAST_ARAB_LETTER) || (codePoint in FIRST_HEBREW_LETTER..LAST_HEBREW_LETTER)
    }
    return false
}

fun String.decorateStringWithSpan( color: String, view: View): SpannableString {
    val spannableString = SpannableString(this)
    spannableString.setSpan(
        RoundedBackgroundColorSpan(
            Color.parseColor(color), 8f.convertDpToPixel(),
            view
        ), 0, this.length , 0
    )
    return spannableString
}

fun Activity.makeSnackbarMessage(messageText: String) {
    Snackbar.make(this.findViewById(R.id.snackbarContainer), messageText, Snackbar.LENGTH_SHORT)
        .show()
}

fun PagingData<RedditItem.RedditPost>.toPagedList() : PagedList<RedditItem> {
    val list = mutableListOf<RedditItem>()
    this.map {
        list.add(it)
    }
    return list as PagedList
}




