package com.instriker.wcre.framework

import android.database.Cursor

class DbResultSet(private val _cursor: Cursor) : IDbResultSet {

    override fun moveToNext(): Boolean {
        return _cursor.moveToNext()
    }

    override fun getInt(index: Int): Int {
        return _cursor.getInt(index)
    }

    override fun getShort(index: Int): Short {
        return _cursor.getShort(index)
    }

    override fun getString(index: Int): String? {
        return _cursor.getString(index)
    }

    override fun isNull(index: Int): Boolean {
        return _cursor.isNull(index)
    }

    override fun close() {
        _cursor.close()
    }
}