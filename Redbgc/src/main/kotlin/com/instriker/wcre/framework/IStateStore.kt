package com.instriker.wcre.framework

interface IStateStore {

    fun putInt(key: String, value: Int)

    fun getInt(key: String, defaultValue: Int): Int

    fun putBoolean(key: String, value: Boolean)

    fun getBoolean(key: String, defaultValue: Boolean): Boolean
}
