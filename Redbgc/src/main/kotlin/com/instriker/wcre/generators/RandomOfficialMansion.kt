package com.instriker.wcre.generators

import java.util.ArrayList
import java.util.Arrays

import com.instriker.wcre.framework.Randomizer
import com.instriker.wcre.generators.Options.*
import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.Mansion
import com.instriker.wcre.services.*

class RandomOfficialMansion(private val _gameContentService: GameContentService) {

    @Throws(Exception::class)
    fun pickRandom(extOptions: ExtensionOptions): MansionCards? {
        val allMansion = mansions
        filterMansionsByExtensions(allMansion, extOptions.fromExtensions)
        val takenMansion = pickRandomMansion(allMansion) ?: return null

        val cards = _gameContentService.getCardsForMansion(takenMansion.id)
        return MansionCards(takenMansion, cards)
    }

    private fun pickRandomMansion(allMansions: List<Mansion>): Mansion? {
        if (allMansions.size == 0) {
            return null
        }
        val index = Randomizer.getRandom(0, allMansions.size - 1)
        return allMansions[index]
    }

    private fun filterMansionsByExtensions(mansions: MutableList<Mansion>, fromExtensions: Collection<Int>) {
        for (index in mansions.indices.reversed()) {
            val current = mansions[index]
            if (!fromExtensions.contains(current.extensionId)) {
                mansions.removeAt(index)
            }
        }
    }

    private val mansions: MutableList<Mansion>
        @Throws(Exception::class)
        get() {
            val mansions = _gameContentService.mansions ?: throw Exception("Could not read mansions.")
            return ArrayList(Arrays.asList(*mansions))
        }
}