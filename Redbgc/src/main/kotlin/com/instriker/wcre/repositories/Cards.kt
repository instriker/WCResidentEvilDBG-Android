package com.instriker.wcre.repositories

import java.util.ArrayList


object Cards {

    fun filterCardsByExtensions(allCards: Collection<Card>, fromExtensions: Collection<Int>): List<Card> {

        val results = ArrayList<Card>(allCards.size)
        for (current in allCards) {
            if (fromExtensions.contains(current.extensionId)) {
                results.add(current)
            }
        }

        return results
    }
}