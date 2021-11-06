package com.instriker.wcre.repositories

import java.util.ArrayList


object Mansions {

    fun filterMansionsByExtensions(allMansions: Collection<Mansion>, fromExtensions: Collection<Int>): List<Mansion> {

        val results = ArrayList<Mansion>(allMansions.size)
        for (current in allMansions) {
            if (fromExtensions.contains(current.extensionId)) {
                results.add(current)
            }
        }

        return results
    }
}