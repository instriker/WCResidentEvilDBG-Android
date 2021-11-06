package com.instriker.wcre.framework

import android.database.sqlite.SQLiteDatabase
import com.instriker.wcre.repositories.GameContentOpenHelper
import java.io.IOException

class DbRepository(private val _gameContentOpenHelper: GameContentOpenHelper) : IDbRepository {
    private var _db: SQLiteDatabase? = null

    @Throws(IOException::class)
    override fun Read(queryString: String): IDbResultSet {
        return Read(queryString, arrayOf<String>())
    }

    @Throws(IOException::class)
    override fun Read(queryString: String, params: Array<String>): IDbResultSet {
        val cursor = sqLiteDatabase.rawQuery(queryString, params)
        return DbResultSet(cursor)
    }

    private val sqLiteDatabase: SQLiteDatabase
        @Throws(IOException::class)
        get() {
            var db = _db;
            if (db == null) {
                db = _gameContentOpenHelper.openDatabase()
                _db = db
            }
            return db
        }

    override fun close() {
        _db?.close()
        _db = null
    }

}
