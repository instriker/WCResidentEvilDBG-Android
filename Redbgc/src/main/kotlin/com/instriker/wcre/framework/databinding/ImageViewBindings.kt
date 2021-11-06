package com.instriker.wcre.framework.databinding

import androidx.databinding.BindingAdapter
import android.widget.ImageView
import android.graphics.drawable.BitmapDrawable

object ImageViewBindings {
    @BindingAdapter("android:src")
    @JvmStatic fun setImageViewResource(imageView: ImageView, resource: Int) {
        val bitmap = imageView.drawable as? BitmapDrawable
        bitmap?.bitmap?.recycle()

        imageView.setImageResource(resource)
    }
}
