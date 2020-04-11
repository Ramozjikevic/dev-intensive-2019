package ru.skillbranch.devintensive.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Rect
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

fun Activity.isKeyboardOpen(): Boolean {
    val softKeyboardHeight = 128

    val rect = Rect()
    window.decorView.getWindowVisibleDisplayFrame(rect)
    val dm = window.decorView.resources.displayMetrics
    val heightDiff = window.decorView.bottom - rect.bottom

    return heightDiff > softKeyboardHeight * dm.density
}

fun Activity.isKeyboardClosed(): Boolean = !isKeyboardOpen()