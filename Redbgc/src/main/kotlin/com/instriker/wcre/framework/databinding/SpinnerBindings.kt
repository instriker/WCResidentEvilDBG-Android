package com.instriker.wcre.framework.databinding

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.ObservableArrayList
import androidx.databinding.adapters.ListenerUtil
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner

import com.instriker.wcre.R

object SpinnerBindings {
    @InverseBindingAdapter(attribute = "selectedItem", event = "selectedItemAttrChanged")
    @JvmStatic
    fun <T> getSelectedItem(spinner: Spinner): T {
        return spinner.selectedItem as T
    }

    @InverseBindingAdapter(attribute = "selectedPosition", event = "selectedPositionAttrChanged")
    @JvmStatic
    fun getSelectedPosition(spinner: Spinner): Int {
        return spinner.selectedItemPosition
    }

    @BindingAdapter("selectedPosition")
    @JvmStatic
    fun setSelectedPosition(spinner: Spinner, position: Int) {
        spinner.setSelection(position, false)
    }

    @BindingAdapter("selectedItem")
    @JvmStatic
    fun <T> setSelectedItem(spinner: Spinner, item: T?) {
        if (item == null) {
            spinner.isSelected = false
        } else {
            val count = spinner.count
            for (i in 0..count - 1) {
                if (spinner.getItemAtPosition(i) === item) {
                    spinner.setSelection(i)
                    break
                }
            }
        }
    }

    @BindingAdapter("selectedItemAttrChanged")
    @JvmStatic
    fun setSelectedItemAttrChanged(spinner: Spinner, inverseBindingListener: InverseBindingListener) {
        registerInverseBindingListener(spinner, inverseBindingListener, R.id.onSpinnerSelectedItemListener)
    }

    @BindingAdapter("selectedPositionAttrChanged")
    @JvmStatic
    fun setSelectedPositionAttrChanged(spinner: Spinner, inverseBindingListener: InverseBindingListener) {
        registerInverseBindingListener(spinner, inverseBindingListener, R.id.onSpinnerSelectedPositionListener)
    }

    private fun registerInverseBindingListener(spinner: Spinner, inverseBindingListener: InverseBindingListener, eventId: Int) {
        val newListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                inverseBindingListener.onChange()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
                inverseBindingListener.onChange()
            }
        }
        ListenerUtil.trackListener<AdapterView.OnItemSelectedListener>(spinner, newListener, eventId)

        val listeners = arrayOf(
                ListenerUtil.getListener<AdapterView.OnItemSelectedListener>(spinner, R.id.onSpinnerSelectedPositionListener),
                ListenerUtil.getListener<AdapterView.OnItemSelectedListener>(spinner, R.id.onSpinnerSelectedItemListener))
                .filter { it != null }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                for (listener in listeners) {
                    listener.onItemSelected(adapterView, view, i, l)
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
                for (listener in listeners) {
                    listener.onNothingSelected(adapterView)
                }
            }
        }
    }

    @BindingAdapter(value = ["itemSource", "itemTemplate", "spinnerTemplate"])
    @JvmStatic
    fun <T> setItemSource(Spinner: Spinner,
                          values: ObservableArrayList<T>?,
                          itemTemplate: Int,
                          spinnerTemplate: Int) {
        val adapter = SpinnerObservableBindingAdapter(values ?: ArrayList(), itemTemplate, spinnerTemplate)
        Spinner.adapter = adapter
    }
}
