package com.instriker.wcre.presentation

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField

import com.instriker.wcre.framework.Bindings
import com.instriker.wcre.framework.BindingsFactory

class PickerViewModel<TItem>(values: Collection<TItem>) {
    val items: ObservableArrayList<TItem>
    val itemChoosen = BindingsFactory.bind<TItem>()

    private var _listener: IPickedResultListener<TItem>? = null

    init {
        items = BindingsFactory.bindCollection<TItem>()
        Bindings.setCollection(items, values)
    }

    fun setPickedResultListener(listener: IPickedResultListener<TItem>) {
        _listener = listener
    }

    fun onItemChoosen() {
        if (_listener != null) {
            _listener!!.onItemChoosen(itemChoosen.get() as TItem)
        }
    }

    fun onCancelled() {
        if (_listener != null) {
            _listener!!.onPickerCancelled()
        }
    }

    interface IPickedResultListener<TItem> {
        fun onItemChoosen(choosen: TItem)

        fun onPickerCancelled()
    }
}
