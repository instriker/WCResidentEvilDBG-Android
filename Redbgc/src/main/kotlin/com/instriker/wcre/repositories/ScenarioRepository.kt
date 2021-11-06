package com.instriker.wcre.repositories

import java.io.IOException
import java.util.ArrayList

import com.instriker.wcre.framework.IDbRepository
import com.instriker.wcre.framework.IDbRepositoryFactory
import com.instriker.wcre.framework.IDbResultSet

class ScenarioRepository(private val _dbRepositoryFactory: IDbRepositoryFactory) {

    private fun joinBy(sources: List<String>, joinedBy: String): String? {
        var result: String? = null
        for (current in sources) {
            if (result == null) {
                result = ""
            } else {
                result += joinedBy
            }
            result += current
        }
        return result
    }

    fun getScenarios(ids: IntArray?, query: String?, gameMode: GameMode?, skillSystem: Boolean?): Array<Scenario> {
        val db = _dbRepositoryFactory.open()

        val criterias = ArrayList<String>()
        val filterParams = ArrayList<String>()

        if (ids != null && ids.size > 0) {
            var filter = ""
            for (curId in ids) {
                if (filter.length > 0) {
                    filter += ","
                }
                filter += Integer.toString(curId)
            }
            filter = "Id in ($filter)"
            criterias.add(filter)
        }

        if (query != null) {
            criterias.add("Name LIKE ?")
            filterParams.add('%' + query + '%')
        }

        if (gameMode != null) {
            criterias.add("GameMode=?")
            filterParams.add(Integer.toString(gameMode.ordinal))
        }

        if (skillSystem != null) {
            criterias.add("SkillModification=?")
            filterParams.add(if (skillSystem) "1" else "0")
        }

        val queryText = StringBuilder()
        queryText.append("SELECT Id,Name,Descriptions,ExtensionId,UseBasicResources,GameMode,SkillModification FROM Scenario")
        val filter = joinBy(criterias, " AND ")
        if (filter != null && filter.length > 0) {
            queryText.append(" WHERE ")
            queryText.append(filter)
        }
        queryText.append(" ORDER BY ID")

        try {
            val cursor = db.Read(queryText.toString(), filterParams.toTypedArray<String>())
            try {
                val results = ArrayList<Scenario>()
                while (cursor.moveToNext()) {
                    val rowResult = Scenario(
                            cursor.getInt(0),
                            cursor.getString(1)!!,
                            cursor.getString(2),
                            cursor.getInt(3),
                            cursor.getShort(4).toInt() != 0,
                            if (cursor.isNull(5)) null else GameMode.values()[cursor.getInt(5)],
                            cursor.getShort(6).toInt() != 0
                    )
                    results.add(rowResult)
                }

                return results.toTypedArray<Scenario>()
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