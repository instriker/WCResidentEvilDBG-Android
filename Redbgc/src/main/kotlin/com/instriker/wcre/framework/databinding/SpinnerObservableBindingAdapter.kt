package com.instriker.wcre.framework.databinding

import android.content.Context
import android.database.DataSetObserver
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SpinnerAdapter

import com.instriker.wcre.BR

import java.util.ArrayList

class SpinnerObservableBindingAdapter<T>(
        private val items: ArrayList<T>,
        private val itemTemplate: Int,
        private val spinnerTemplate: Int) : SpinnerAdapter {

    private var inflater: LayoutInflater? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return bindView(position, convertView, parent, spinnerTemplate)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return bindView(position, convertView, parent, itemTemplate)
    }

    private fun bindView(position: Int, convertView: View?, parent: ViewGroup, template: Int): View {
        // TODO : Use convertView
        val binding = DataBindingUtil.inflate<ViewDataBinding>(getInflater(parent), template, parent, false)
        val itemViewModel = this.getItem(position)
        binding.setVariable(BR.mainViewModel, itemViewModel)

        return binding.root
    }

    private fun getInflater(parent: ViewGroup): LayoutInflater {
        val result = this.inflater;
        if (result == null) {
            this.inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            return getInflater(parent)
        }
        return result
    }

    override fun registerDataSetObserver(observer: DataSetObserver) {}

    override fun unregisterDataSetObserver(observer: DataSetObserver) {}

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Object {
        return items[position] as Object
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun isEmpty(): Boolean {
        return count == 0
    }
}