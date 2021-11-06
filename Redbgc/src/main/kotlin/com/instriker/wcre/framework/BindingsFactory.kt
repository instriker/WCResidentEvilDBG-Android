package com.instriker.wcre.framework

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableFloat
import androidx.databinding.ObservableInt

object BindingsFactory {
    fun bindBoolean(): ObservableBoolean {
        return ObservableBoolean()
    }

    fun bindBoolean(defaultValue: Boolean): ObservableBoolean {
        return ObservableBoolean(defaultValue)
    }

    fun bindInteger(): ObservableInt {
        return ObservableInt()
    }

    fun bindInteger(defaultValue: Int): ObservableInt {
        return ObservableInt(defaultValue)
    }

    fun bindString(): ObservableField<String> {
        return ObservableField()
    }

    fun bindString(defaultValue: String): ObservableField<String> {
        return ObservableField(defaultValue)
    }

    fun bindFloat(): ObservableFloat {
        return ObservableFloat()
    }

    fun bindFloat(defaultValue: Float): ObservableFloat {
        return ObservableFloat(defaultValue)
    }

    fun <T> bindCollection(): ObservableArrayList<T> {
        return ObservableArrayList()
    }

    fun <T> bind(): ObservableField<T> {
        return ObservableField()
    }

    fun <T> bind(defaultValue: T): ObservableField<T> {
        return ObservableField(defaultValue)
    }
}
