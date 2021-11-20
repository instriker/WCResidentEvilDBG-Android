package com.instriker.wcre.generators.Options

import java.util.ArrayList

import com.instriker.wcre.config.Constants

class ExtensionOptions {
    private val _fromExtensions: ArrayList<Int>

    init {
        _fromExtensions = ArrayList<Int>()
    }

    val fromExtensions: ArrayList<Int>
        get() {
            val copy = ArrayList<Int>(_fromExtensions.size)
            copy.addAll(_fromExtensions)
            return copy
        }

    fun setFromExtensions(fromExtensions: Collection<Int>) {
        _fromExtensions.clear()
        _fromExtensions.addAll(fromExtensions)
    }
}