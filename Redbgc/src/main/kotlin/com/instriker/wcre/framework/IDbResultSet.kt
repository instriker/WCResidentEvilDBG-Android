package com.instriker.wcre.framework

interface IDbResultSet {
    fun moveToNext(): Boolean

    fun getInt(index: Int): Int

    fun getString(index: Int): String?

    fun getShort(index: Int): Short

    fun isNull(index: Int): Boolean

    fun close()
}
