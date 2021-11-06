package com.instriker.wcre.repositories

import java.io.IOException
import java.util.ArrayList

import com.instriker.wcre.framework.IDbRepository
import com.instriker.wcre.framework.IDbRepositoryFactory
import com.instriker.wcre.framework.IDbResultSet

class MansionRepository(private val _dbRepositoryFactory: IDbRepositoryFactory) {

    fun getMansions(ids: IntArray?): Array<Mansion> {

        var filter: String? = ""

        if (ids != null && ids.size > 0) {
            for (curId in ids) {
                if (filter!!.length > 0) {
                    filter += ","
                }
                filter += Integer.toString(curId)
            }
            filter = "Id in ($filter)"
        }

        val queryText = StringBuilder()
        queryText.append("SELECT Id,Name,Descriptions,ExtensionId,Difficulty FROM Mansion")
        if (filter != null && filter.length > 0) {
            queryText.append(" WHERE ")
            queryText.append(filter)
        }
        queryText.append(" ORDER BY ExtensionId,Id")

        val db = _dbRepositoryFactory.open()
        try {
            val cursor = db.Read(queryText.toString())
            try {
                val results = ArrayList<Mansion>()
                while (cursor.moveToNext()) {
                    val rowResult = Mansion(
                            cursor.getInt(0),
                            cursor.getString(1)!!,
                            cursor.getString(2),
                            cursor.getInt(3),
                            if (cursor.isNull(4)) null else MansionDifficulty.values()[cursor.getInt(4)])
                    results.add(rowResult)
                }

                return results.toTypedArray<Mansion>()
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