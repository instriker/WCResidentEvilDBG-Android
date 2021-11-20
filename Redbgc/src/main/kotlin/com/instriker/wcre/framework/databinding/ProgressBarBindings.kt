package com.instriker.wcre.framework.databinding

import androidx.databinding.BindingAdapter
import android.graphics.Rect
import android.widget.ProgressBar

object ProgressBarBindings {

    @BindingAdapter("progressDrawableId")
    @JvmStatic fun setProgressDrawableId(view: ProgressBar, newValue: Int) {
        val bounds = view.progressDrawable.bounds
        val progress = view.progress

        view.progressDrawable = view.context.resources.getDrawable(newValue)

        view.progressDrawable.bounds = bounds

        val offset = if (progress >= 1) -1 else 1
        view.progress = progress + offset
        view.progress = progress
    }
}
