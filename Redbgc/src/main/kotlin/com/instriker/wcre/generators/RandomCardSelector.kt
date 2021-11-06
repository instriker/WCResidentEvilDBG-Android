package com.instriker.wcre.generators

import java.util.ArrayList

import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.CardType

class RandomCardSelector(private val _expectedCount: Int, private val _ofType: CardType) : ICardSelector {

    override fun chooseCards(toChooseFrom: Collection<Card>): Array<Card> {
        val choosen = ArrayList<Card>(_expectedCount)
        var actualCount = 0

        if (this._expectedCount > 0) {
            for (card in toChooseFrom) {
                val type = card.cardType
                if (type == this._ofType) {
                    choosen.add(card)

                    actualCount++
                    if (actualCount == _expectedCount) {
                        break
                    }
                }
            }
        }

        return choosen.toTypedArray<Card>()
    }
}