package com.instriker.wcre.framework

import androidx.databinding.ObservableArrayList

import java.util.Arrays

object Bindings {
    fun <T> addToCollection(bindableCollection: ObservableArrayList<T>, newValue: T) {
        bindableCollection.add(newValue)
    }

    fun <T> addToCollection(bindableCollection: ObservableArrayList<T>, newValue: Collection<T>) {
        bindableCollection.addAll(newValue)
    }

    fun <T> setCollection(bindableCollection: ObservableArrayList<T>, newValue: Collection<T>) {
        clearCollection(bindableCollection)
        bindableCollection.addAll(newValue)
    }

    fun <T> setCollection(bindableCollection: ObservableArrayList<T>, newValue: Array<T>) {
        setCollection(bindableCollection, Arrays.asList(*newValue))
    }

    fun <T> clearCollection(bindableCollection: ObservableArrayList<T>) {
        bindableCollection.clear()
    }

    fun <T> isEmptyCollection(bindableCollection: ObservableArrayList<T>): Boolean {
        return bindableCollection.isEmpty()
    }

    fun <T> getItemAt(bindableCollection: ObservableArrayList<T>, index: Int): T {
        return bindableCollection[index]
    }

    fun <T> replaceItemAt(bindableCollection: ObservableArrayList<T>, index: Int, newValue: T) {
        bindableCollection[index] = newValue
    }

    fun <T> getItems(bindableCollection: ObservableArrayList<T>): Iterable<T> {
        return bindableCollection
    }

    inline fun <reified T> toArray(bindableCollection: ObservableArrayList<T>): Array<T> {
        val array = bindableCollection.toTypedArray()
        return array
    }

    fun <T> getSize(bindableCollection: ObservableArrayList<T>): Int {
        return bindableCollection.size
    }
}