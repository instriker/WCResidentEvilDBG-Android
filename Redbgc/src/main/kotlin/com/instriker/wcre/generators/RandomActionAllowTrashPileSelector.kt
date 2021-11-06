package com.instriker.wcre.generators

import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.CardType

class RandomActionAllowTrashPileSelector(expectedCount: Int) : RandomPileSelector(expectedCount, CardType.Action) {

    override fun isValidPile(pile: Array<Card>): Boolean {
        return super.isValidPile(pile) && pile[0].allowTrash
    }
}