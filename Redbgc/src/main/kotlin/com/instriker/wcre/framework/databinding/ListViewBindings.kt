package com.instriker.wcre.framework.databinding

import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import android.widget.AdapterView
import android.widget.ListView
import java.util.*

object ListViewBindings {

    @BindingAdapter(value = *arrayOf("itemSource", "itemTemplate"))
    @JvmStatic fun <T : Any> setItemSource(listView: ListView,
                                           newValues: ObservableArrayList<T>?, newItemTemplate: Int) {
        val bindingValues = newValues ?: ArrayList<T>()
        val adapter = ObservableBindingAdapter(bindingValues, newItemTemplate)
        listView.adapter = adapter
    }

    @BindingAdapter("onItemClicked")
    @JvmStatic fun <T> setOnItemClicked(listView: ListView,
                                        newConsumer: (T, Int) -> Unit) {
        val listener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val item = listView.getItemAtPosition(position) as T
            newConsumer(item, position)
        }
        listView.onItemClickListener = listener
    }
}
