package com.instriker.wcre.generators

import com.instriker.wcre.repositories.Card

class RandomAllowExtraExplorePileSelector(expectedCount: Int) : BasePileSelector(expectedCount) {

    override fun isValidPile(pile: Array<Card>): Boolean {
        return pile[0].allowExtraExplore
    }
}