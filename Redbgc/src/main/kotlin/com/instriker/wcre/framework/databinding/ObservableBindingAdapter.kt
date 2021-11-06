package com.instriker.wcre.framework.databinding

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

import com.instriker.wcre.BR

import java.util.ArrayList

class ObservableBindingAdapter<T : Any>(
        private val items: ArrayList<T>,
        private val itemTemplate: Int) : BaseAdapter() {
    private var inflater: LayoutInflater? = null

    init {
        val observable = items as? ObservableArrayList<T>

        if (observable != null) {
            val listener = object : ObservableList.OnListChangedCallback<ObservableList<T>>() {
                override fun onChanged(observableList: ObservableList<T>) {
                    notifyDataSetChanged()
                }

                override fun onItemRangeChanged(observableList: ObservableList<T>, positionStart: Int, itemCount: Int) {
                    notifyDataSetChanged()
                }

                override fun onItemRangeInserted(observableList: ObservableList<T>, positionStart: Int, itemCount: Int) {
                    notifyDataSetChanged()
                }

                override fun onItemRangeMoved(observableList: ObservableList<T>, fromPosition: Int, toPosition: Int, itemCount: Int) {
                    notifyDataSetChanged()
                }

                override fun onItemRangeRemoved(observableList: ObservableList<T>, positionStart: Int, itemCount: Int) {
                    notifyDataSetChanged()
                }
            }
            observable.addOnListChangedCallback(listener)
        }
    }

    override fun getCount(): Int {
        return this.items.size
    }

    override fun getItem(position: Int): Any {
        return this.items[position]
    }

    override fun getItemId(position: Int): Long {
        // TODO ...
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (this.inflater == null) {
            this.inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater!!, itemTemplate, parent, false)
        val itemViewModel = this.getItem(position)
        binding.setVariable(BR.mainViewModel, itemViewModel)

        return binding.root
    }
}