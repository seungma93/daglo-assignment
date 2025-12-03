package com.seungma.daglo.presenter

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.seungma.daglo.databinding.SnackbarCustomBinding


class CustomSnackbar private constructor(private val view: View, message: String, duration: Int) {
    private val snackbar by lazy {
        Snackbar.make(view, "", duration)
    }
    @SuppressLint("RestrictedApi")
    private val snackbarLayout = snackbar.view as (Snackbar.SnackbarLayout)

    private val context = view.context
    private lateinit var binding: SnackbarCustomBinding

    companion object {
        fun make(view: View, message: String, duration: Int): CustomSnackbar {
            return CustomSnackbar(view, message, duration)
        }
    }
    init {
        setSnackbarView()
        setSnackbarText(message)
    }

    private fun setSnackbarView() {
        binding = SnackbarCustomBinding.inflate(LayoutInflater.from(context), snackbarLayout, false)

        snackbarLayout.run {
            removeAllViews()
            setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
            addView(binding.root)
        }

    }

    fun setPadding(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0): CustomSnackbar {
        snackbarLayout.setPadding(left, top, right, bottom)
        return this
    }

    @SuppressLint("RestrictedApi")
    fun setMargin(leftDp: Int = 0, topDp: Int = 0, rightDp: Int = 0, bottomDp: Int = 0): CustomSnackbar {
        val leftPx = context.dpToPx(leftDp)
        val topPx = context.dpToPx(topDp)
        val rightPx = context.dpToPx(rightDp)
        val bottomPx = context.dpToPx(bottomDp)

        val params = snackbarLayout.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(leftPx, topPx, rightPx, bottomPx)
        snackbarLayout.layoutParams = params
        return this
    }

    fun setAnchorView(anchorViewId: Int): CustomSnackbar {
        snackbar.setAnchorView(anchorViewId)
        return this
    }


    private fun setSnackbarText(message: String) {
        binding.tvSnackbar.text = message
    }

    fun show() {
        snackbar.show()
    }

    fun Context.dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

}