package com.instriker.wcre.generators

import com.instriker.wcre.repositories.Card

class BasicResourcesSelector : IPileSelector {

    override fun choosePiles(toChooseFrom: Collection<Array<Card>>): Array<Array<Card>> {
        val chosen = ArrayList<Array<Card>>()
        var takeFromExtension: Int? = null

        for (pile in toChooseFrom) {
            val card = pile[0]
            val isBasicResource = card.basicResourcePileGroup != null
            if (isBasicResource) {
                takeFromExtension = takeFromExtension ?: card.extensionId

                if (takeFromExtension!! == card.extensionId) {
                    chosen.add(pile)
                }
            }
        }

        return chosen.toTypedArray<Array<Card>>()
    }
}
