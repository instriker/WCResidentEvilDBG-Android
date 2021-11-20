package com.instriker.wcre.repositories

import java.io.IOException
import java.util.*

import com.instriker.wcre.framework.IDbRepository
import com.instriker.wcre.framework.IDbRepositoryFactory
import com.instriker.wcre.framework.IDbResultSet

class StandardMutualyExclusiveCardRepository(private val _dbRepositoryFactory: IDbRepositoryFactory) {

    val standardMutualyExclusiveCard: Array<IntArray>
        get() {
            val db = _dbRepositoryFactory.open()
            try {
                val cursor = db.Read("SELECT Card1,Card2,Card3 FROM StandardMutualyExclusiveCard ORDER BY Id")

                try {
                    val exclusives = ArrayList<IntArray>()

                    while (cursor.moveToNext()) {
                        val card1 = cursor.getInt(0)
                        val card2 = cursor.getInt(1)
                        val card3 = cursor.getInt(2)

                        exclusives.add(intArrayOf(card1, card2, card3))
                    }

                    return exclusives.toTypedArray<IntArray>()
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
