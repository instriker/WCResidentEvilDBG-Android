package com.instriker.wcre.repositories

import java.io.IOException
import java.util.ArrayList

import com.instriker.wcre.framework.IDbRepository
import com.instriker.wcre.framework.IDbRepositoryFactory
import com.instriker.wcre.framework.IDbResultSet

class ExtensionRepository(private val _dbRepositoryFactory: IDbRepositoryFactory) {

    val extensions: Array<GameExtension>
        get() {
            val db = _dbRepositoryFactory.open()

            try {
                val cursor = db.Read("SELECT Id,Name FROM Extension ORDER BY Id")
                try {

                    val results = ArrayList<GameExtension>()
                    while (cursor.moveToNext()) {
                        val rowResult = GameExtension(cursor.getInt(0), cursor.getString(1)!!)
                        results.add(rowResult)
                    }
                    return results.toTypedArray()
                } finally {
                    cursor.close()
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
                throw RuntimeException("Failed to read data".plus(ex.message))
            } finally {
                db.close()
            }
        }
}
