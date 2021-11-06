package com.instriker.wcre.framework.databinding

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.databinding.ObservableList.OnListChangedCallback
import androidx.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

import com.instriker.wcre.BR
import com.instriker.wcre.R

import java.util.ArrayList

class PagerObservableBindingAdapter<T, TR : CharSequence>(
        private val items: ArrayList<T>,
        private val itemTemplate: Int,
        private val getItemTitle: (T) -> TR) : PagerAdapter() {

    private val listener: OnListChangedCallback<ObservableList<T>>?
    private var inflater: LayoutInflater? = null

    init {
        val observable = if (this.items is ObservableArrayList<*>)
            this.items
        else
            null

        if (observable == null) {
            this.listener = null
        } else {
            this.listener = object : OnListChangedCallback<ObservableList<T>>() {
                override fun onChanged(sender: ObservableList<T>?) {
                    notifyDataSetChanged()
                }

                override fun onItemRangeChanged(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {
                    notifyDataSetChanged()
                }

                override fun onItemRangeMoved(sender: ObservableList<T>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                    notifyDataSetChanged()
                }

                override fun onItemRangeRemoved(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {
                    notifyDataSetChanged()
                }

                override fun onItemRangeInserted(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {
                    notifyDataSetChanged()
                }
            }
            observable.addOnListChangedCallback(this.listener)
        }
    }

    override fun getCount(): Int {
        return this.items.size
    }

    private fun getItem(position: Int): T? {
        return if (position < this.items.size)
            this.items[position]
        else
            null
    }

    override fun getPageTitle(position: Int): CharSequence {
        val item = this.getItem(position)
        return if (item != null)
            this.getItemTitle(item)
        else
            ""
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        this.inflater = this.inflater ?: container.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater!!, itemTemplate, container, false)
        val root = binding.root

        val itemViewModel = this.getItem(position)
        binding.setVariable(BR.mainViewModel, itemViewModel)
        root.setTag(R.id.viewPagerItemPosition, position)

        container.addView(root)
        return root
    }

    override fun getPageWidth(position: Int): Float {
        return 1.0f
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }
}