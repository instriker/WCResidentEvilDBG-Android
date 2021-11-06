package com.instriker.wcre.framework

import android.os.Bundle

class BundleStateStore(private val _savedInstanceState: Bundle) : IStateStore {

    override fun putInt(key: String, value: Int) {
        _savedInstanceState.putInt(key, value)
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return _savedInstanceState.getInt(key, defaultValue)
    }

    override fun putBoolean(key: String, value: Boolean) {
        _savedInstanceState.putBoolean(key, value)
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return _savedInstanceState.getBoolean(key, defaultValue)
    }
}
