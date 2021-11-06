package com.instriker.wcre.framework.databinding

import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import android.view.View

object ViewBindings {
    @BindingAdapter("onBind")
    @JvmStatic fun setOnBind(view: View, newValue: () -> Any) {
        newValue()
    }
}
