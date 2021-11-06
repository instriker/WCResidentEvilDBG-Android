package com.instriker.wcre.repositories

import java.io.IOException
import java.util.*

import com.instriker.wcre.framework.IDbRepositoryFactory

class StandardPilesRepository(private val _dbRepositoryFactory: IDbRepositoryFactory) {

    val list: Array<IntArray>
        get() {
            val db = _dbRepositoryFactory.open()
            try {
                val cursor = db.Read("SELECT PileId,CardId FROM StandardCardPile ORDER BY PileId")

                try {
                    val piles = ArrayList<ArrayList<Int>>()
                    var currentPile: ArrayList<Int>? = null
                    var currentPileId: Int? = null
                    while (cursor.moveToNext()) {
                        val pileId = cursor.getInt(0)
                        val cardId = cursor.getInt(1)

                        if (currentPileId == null || currentPileId !== pileId) {
                            currentPileId = pileId
                            currentPile = ArrayList<Int>()
                            piles.add(currentPile)
                        }
                        currentPile!!.add(cardId)
                    }

                    val interim = ArrayList<IntArray>()
                    for (pile in piles) {
                        interim.add(pile.toList().toIntArray())
                    }
                    return interim.toTypedArray<IntArray>()
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
