package com.instriker.wcre.framework.databinding

import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import android.view.View

object KotlinBindings {
    @BindingConversion
    @JvmStatic fun convertBooleanToViewVisibility(isVisible: Boolean): Int {
        return if (isVisible) View.VISIBLE else View.GONE
    }
}
