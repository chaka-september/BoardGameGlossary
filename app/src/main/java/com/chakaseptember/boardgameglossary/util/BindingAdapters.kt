package com.chakaseptember.boardgameglossary.util

import android.view.View
import androidx.databinding.BindingAdapter


/**
 * Binding adapter used to hide the spinner once data is available.
 */
@BindingAdapter("isNetworkError", "words")
fun hideIfNetworkError(view: View, isNetWorkError: Boolean, words: Any?) {
    view.visibility = if (words != null) View.GONE else View.VISIBLE

    if (isNetWorkError) {
        view.visibility = View.GONE
    }
}