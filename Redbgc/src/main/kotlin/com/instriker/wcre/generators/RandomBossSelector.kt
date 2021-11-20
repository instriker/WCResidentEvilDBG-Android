package com.instriker.wcre.generators

import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.CardType

class RandomBossSelector : ICardSelector {

    override fun chooseCards(toChooseFrom: Collection<Card>): Array<Card> {
        for (card in toChooseFrom) {
            if (card.cardType == CardType.InfectedBoss) {
                return arrayOf(card)
            }
        }

        return arrayOf<Card>();
    }
}
