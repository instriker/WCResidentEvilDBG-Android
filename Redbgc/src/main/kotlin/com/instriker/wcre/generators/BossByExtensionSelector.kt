package com.instriker.wcre.generators

import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.CardType

class BossByExtensionSelector(private val _extensionId: Int) : ICardSelector {

    override fun chooseCards(toChooseFrom: Collection<Card>): Array<Card> {
        for (card in toChooseFrom) {
            if (card.cardType == CardType.InfectedBoss && card.extensionId == _extensionId) {
                return arrayOf(card)
            }
        }

        return arrayOf<Card>()
    }
}