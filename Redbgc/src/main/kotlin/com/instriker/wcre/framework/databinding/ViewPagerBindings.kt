package com.instriker.wcre.framework.databinding

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.ObservableArrayList
import androidx.databinding.adapters.ListenerUtil
import androidx.viewpager.widget.ViewPager

import com.instriker.wcre.R

object ViewPagerBindings {
    @BindingAdapter("currentItem")
    @JvmStatic
    fun setCurrentItem(viewPager: ViewPager, position: Int) {
        viewPager.currentItem = position
    }

    @InverseBindingAdapter(attribute = "currentItem", event = "currentItemAttrChanged")
    @JvmStatic
    fun getCurrentItem(viewPager: ViewPager): Int {
        return viewPager.currentItem
    }

    @BindingAdapter("currentItemAttrChanged")
    @JvmStatic
    fun setCurrentItemListeners(view: ViewPager, inverseBindingListener: InverseBindingListener) {
        val newListener = object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                inverseBindingListener.onChange()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        }

        val oldListener = ListenerUtil.trackListener<ViewPager.OnPageChangeListener>(view, newListener, R.id.onPageChangeListener)
        if (oldListener != null) {
            view.removeOnPageChangeListener(oldListener)
        }
        if (newListener != null) {
            view.addOnPageChangeListener(newListener)
        }
    }

    @BindingAdapter("itemTitle")
    @JvmStatic
    fun <T, TR : CharSequence> setItemSource(viewPager: ViewPager, newItemTitle: (T) -> TR) {
        // Dummy, just so databinding build
    }

    @BindingAdapter(value = *arrayOf("itemSource", "itemTemplate", "itemTitle"))
    @JvmStatic
    fun <T, TR : CharSequence> setItemSource(viewPager: ViewPager,
                                             newValues: ObservableArrayList<T>?,
                                             newItemTemplate: Int,
                                             newItemTitle: (T) -> TR) {
        val adapter = PagerObservableBindingAdapter(newValues ?: ArrayList<T>(), newItemTemplate, newItemTitle)
        viewPager.adapter = adapter
    }
}