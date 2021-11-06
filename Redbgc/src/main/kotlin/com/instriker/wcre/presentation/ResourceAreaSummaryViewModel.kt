package com.instriker.wcre.presentation

import androidx.databinding.ObservableInt

import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.CardType

class ResourceAreaSummaryViewModel {
    var totalPiles = BindingsFactory.bindInteger()
    var totalAmmunition = BindingsFactory.bindInteger()
    var totalWeapon = BindingsFactory.bindInteger()
    var totalItem = BindingsFactory.bindInteger()
    var totalAction = BindingsFactory.bindInteger()

    fun generateSummary(piles: Array<Array<Card>>) {
        generageTotalPiles(piles)
        this.totalAmmunition.set(generageTotalAmount(piles, CardType.Ammunition))
        this.totalWeapon.set(generageTotalAmount(piles, CardType.Weapon))
        this.totalItem.set(generageTotalAmount(piles, CardType.Item))
        this.totalAction.set(generageTotalAmount(piles, CardType.Action))
    }

    private fun generageTotalPiles(piles: Array<Array<Card>>) {
        this.totalPiles.set(piles.size)
    }

    private fun generageTotalAmount(piles: Array<Array<Card>>, type: CardType): Int {
        var count = 0
        for (current in piles) {
            val card = current[0]
            if (card.cardType == type) {
                count++
            }
        }
        return count
    }
}
