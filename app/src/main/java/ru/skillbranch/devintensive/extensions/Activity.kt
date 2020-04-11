package ru.skillbranch.devintensive.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard() {
    val inputMethodManager  = this.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    var focusedView = this.currentFocus
    if (focusedView == null) {
        focusedView = View(this)
    }
    inputMethodManager.hideSoftInputFromWindow(focusedView.windowToken, 0)
}