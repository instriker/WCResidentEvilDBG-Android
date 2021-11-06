package com.instriker.wcre.repositories

import com.instriker.wcre.framework.IDbRepositoryFactory
import com.instriker.wcre.framework.IDbResultSet
import java.io.IOException
import java.util.*

class CardRepository(private val _dbRepositoryFactory: IDbRepositoryFactory) {

    fun getCardIdsForType(types: Array<CardType>?): IntArray? {
        var filter: String? = null
        if (types == null || types.size == 0) {
            filter = null
        } else {
            for (curType in types) {
                if (filter == null) {
                    filter = ""
                } else {
                    filter += ","
                }
                filter += Integer.toString(curType.ordinal)
            }
            filter = "Type in ($filter)"
        }

        val queryText = StringBuilder()
        queryText.append("SELECT Id FROM Card")
        if (filter != null && filter.length > 0) {
            queryText.append(" WHERE ")
            queryText.append(filter)
        }
        queryText.append(" ORDER BY ID")

        val db = _dbRepositoryFactory.open()
        try {
            val cursor = db.Read(queryText.toString())
            try {
                val results = ArrayList<Int>()
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(0)
                    results.add(id)
                }

                return results.toIntArray()
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

    fun getCustomInventoryCardsForCharacter(characterId: Int): Array<Card> {
        val db = _dbRepositoryFactory.open()

        try {
            val queryString = "Select c.ID, c.CardNo, c.Name, c.Type, sc.Quantity, c.ExtensionId, c.SlotNo, c.Decorations, c.WeaponCategory, c.BasicResourcePileGroup, c.Cost, c.XpCost, c.Health, c.PicBig, c.Damage, c.AllowExtraExplore, c.AllowTrash "
                    .plus("From CustomStartingInventory sc " + "Inner Join Card c on sc.InventoryCardId=c.ID " + "Where sc.CharacterCardId=? " + "Order by sc.Id")
            val cursor = db.Read(queryString, arrayOf(Integer.toString(characterId)))

            try {
                val results = ArrayList<Card>()
                while (cursor.moveToNext()) {
                    val quantity = cursor.getInt(4)
                    val rowResult = materializeCard(cursor, quantity)
                    results.add(rowResult)
                }

                return results.toTypedArray<Card>()
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

    fun getCards(ids: IntArray?): Array<Card> {
        var filter: String? = null
        if (ids == null || ids.size == 0) {
            filter = null
        } else {
            for (curId in ids) {
                if (filter == null) {
                    filter = ""
                } else {
                    filter += ","
                }
                filter += Integer.toString(curId)
            }
            filter = "Id in ($filter)"
        }

        val queryText = StringBuilder()
        queryText.append("SELECT Id,CardNo,Name,Type,Quantity,ExtensionId,SlotNo,Decorations,WeaponCategory,BasicResourcePileGroup,Cost,XpCost,Health,PicBig,Damage,AllowExtraExplore,AllowTrash FROM Card")
        if (filter != null && filter.length > 0) {
            queryText.append(" WHERE ")
            queryText.append(filter)
        }
        queryText.append(" ORDER BY ID")

        val db = _dbRepositoryFactory.open()
        try {
            val cursor = db.Read(queryText.toString())

            try {
                val results = ArrayList<Card>()
                while (cursor.moveToNext()) {
                    val quantity = cursor.getInt(4)
                    val rowResult = materializeCard(cursor, quantity)
                    results.add(rowResult)
                }

                return results.toTypedArray<Card>()
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

    //@SuppressLint("UseSparseArrays")
    // Needs to iterate through it
    fun getCardPilesForScenario(id: Int): Array<Array<Card>> {
        val db = _dbRepositoryFactory.open()

        try {
            val queryString = "Select c.ID, c.CardNo, c.Name, c.Type, c.Quantity, c.ExtensionId, c.SlotNo, c.Decorations, c.WeaponCategory, c.BasicResourcePileGroup, c.Cost, c.XpCost, c.Health, c.PicBig, c.Damage, c.AllowExtraExplore, c.AllowTrash, sc.PileGroup "
                    .plus("From ScenarioCard sc " + "Inner Join Card c on sc.CardId = c.ID " + "Where sc.ScenarioId = ? " + "Order by sc.Id")
            val cursor = db.Read(queryString, arrayOf(Integer.toString(id)))

            try {
                var seq = 0
                val cardSequences = HashMap<Int, Int>()

                val results = ArrayList<Array<Card>>()
                val piles = HashMap<Int, ArrayList<Card>>()

                while (cursor.moveToNext()) {
                    val quantity = cursor.getInt(4)
                    val rowResult = materializeCard(cursor, quantity)
                    cardSequences.put(rowResult.id, seq++)
                    val pileGroup = if (cursor.isNull(17)) null else Integer.valueOf(cursor.getInt(17))
                    if (pileGroup == null) {
                        results.add(arrayOf(rowResult))
                    } else {
                        var pile: ArrayList<Card>? = piles[pileGroup]
                        if (pile == null) {
                            pile = ArrayList<Card>()
                            piles.put(pileGroup, pile)
                        }
                        pile.add(rowResult)
                    }
                }

                for (pile in piles.values) {
                    results.add(pile.toTypedArray<Card>())
                }

                val idToSeq = cardSequences
                Collections.sort(results) { lhs, rhs ->
                    val firstCard = lhs[0]
                    val secondCard = rhs[0]
                    idToSeq.getValue(firstCard.id) - idToSeq.getValue(secondCard.id)
                }

                return results.toTypedArray<Array<Card>>()

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

    fun getCardsForMansion(id: Int): Array<Card> {
        val db = _dbRepositoryFactory.open()

        try {
            val queryString = "Select c.ID, c.CardNo, c.Name, c.Type, sc.Quantity, c.ExtensionId, c.SlotNo, c.Decorations, c.WeaponCategory, c.BasicResourcePileGroup, c.Cost, c.XpCost, c.Health, c.PicBig, c.Damage, c.AllowExtraExplore, c.AllowTrash "
                    .plus("From MansionCard sc " + "Inner Join Card c on sc.CardId=c.ID " + "Where sc.MansionId=? " + "Order by sc.Id")
            val cursor = db.Read(queryString, arrayOf(Integer.toString(id)))

            try {
                val results = ArrayList<Card>()
                while (cursor.moveToNext()) {
                    val quantity = cursor.getInt(4)
                    val rowResult = materializeCard(cursor, quantity)
                    results.add(rowResult)
                }

                return results.toTypedArray<Card>()
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

    //@SuppressLint("UseSparseArrays")
    // Needs to iterate through it
    fun getBasicResourcesPiles(extensionId: Int?): Array<Array<Card>> {

        val db = _dbRepositoryFactory.open()
        try {
            val filter = "ExtensionId = ? And BasicResourcePileGroup is not null"
            val filterArgs = arrayOf(extensionId!!.toString())

            val queryText = StringBuilder()
            queryText.append("SELECT ID,CardNo,Name,Type,Quantity,ExtensionId,SlotNo,Decorations,WeaponCategory,BasicResourcePileGroup,Cost,XpCost,Health,PicBig,Damage,AllowExtraExplore,AllowTrash FROM Card")
            if (filter != null && filter.length > 0) {
                queryText.append(" WHERE ")
                queryText.append(filter)
            }
            queryText.append(" ORDER BY BasicResourcePileGroup")

            val cursor = db.Read(queryText.toString(), filterArgs)

            try {
                val results = ArrayList<Array<Card>>()
                val piles = HashMap<Int, ArrayList<Card>>()

                while (cursor.moveToNext()) {
                    val quantity = cursor.getInt(4)
                    val rowResult = materializeCard(cursor, quantity)
                    val pileGroup = if (cursor.isNull(9)) null else Integer.valueOf(cursor.getInt(9))
                    if (pileGroup == null) {
                        results.add(arrayOf(rowResult))
                    } else {
                        var pile: ArrayList<Card>? = piles[pileGroup]
                        if (pile == null) {
                            pile = ArrayList<Card>()
                            piles.put(pileGroup, pile)
                        }
                        pile.add(rowResult)
                    }
                }

                for (pile in piles.values) {
                    results.add(pile.toTypedArray<Card>())
                }

                return results.toTypedArray<Array<Card>>()
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

    private fun materializeCard(cursor: IDbResultSet, quantity: Int): Card {
        val rowResult = Card(
                cursor.getInt(0),
                cursor.getString(1)!!,
                cursor.getString(2)!!,
                CardType.values()[cursor.getInt(3)],
                cursor.getInt(5),
                if (cursor.isNull(6)) null else Integer.valueOf(cursor.getInt(6)),
                if (cursor.isNull(7)) null else Integer.valueOf(cursor.getInt(7)),
                if (cursor.isNull(8)) null else WeaponCategory.values()[cursor.getInt(8)],
                quantity,
                if (cursor.isNull(9)) null else Integer.valueOf(cursor.getInt(9)),
                if (cursor.isNull(10)) null else Integer.valueOf(cursor.getInt(10)),
                if (cursor.isNull(11)) null else Integer.valueOf(cursor.getInt(11)),
                if (cursor.isNull(12)) null else Integer.valueOf(cursor.getInt(12)),
                if (cursor.isNull(13)) null else cursor.getString(13),
                if (cursor.isNull(14)) null else Integer.valueOf(cursor.getInt(14)),
                cursor.getShort(15).toInt() != 0,
                cursor.getShort(16).toInt() != 0)
        return rowResult
    }

    fun getCardTypeCountByExtensions(cardType: CardType): Map<Int, Int> {
        val db = _dbRepositoryFactory.open()

        try {
            val queryString = "SELECT ex.Id, Sum(c.Quantity) FROM Extension ex left join Card c on ex.Id = c.ExtensionId and c.Type = ? Group By ex.Id"
            val cursor = db.Read(queryString, arrayOf(Integer.toString(cardType.ordinal)))
            try {
                val results = HashMap<Int, Int>()

                while (cursor.moveToNext()) {
                    val extensionId = cursor.getInt(0)
                    val qty = cursor.getInt(1)
                    results.put(extensionId, qty)
                }

                return results
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

    fun getTotalDistinctCardsCount(cardType: CardType): Int {
        val db = _dbRepositoryFactory.open()

        try {
            val queryString = "SELECT Count(*) FROM Card c WHERE c.Type=?"
            val cursor = db.Read(queryString, arrayOf(Integer.toString(cardType.ordinal)))
            try {
                if (cursor.moveToNext()) {
                    return cursor.getInt(0)
                }

                return 0
            } finally {
                cursor.close()
            }
        } catch (ex: IOException) {
            return 0
        } finally {
            db.close()
        }
    }
}