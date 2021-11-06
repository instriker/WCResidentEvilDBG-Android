package com.instriker.wcre.generators

import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.CardType


open class RandomPileSelector(expectedCount: Int, private val _ofType: CardType?) : BasePileSelector(expectedCount), IPileSelector {

    override fun isValidPile(pile: Array<Card>): Boolean {
        val type = pile[0].cardType
        return this._ofType == null || type == this._ofType
    }
}