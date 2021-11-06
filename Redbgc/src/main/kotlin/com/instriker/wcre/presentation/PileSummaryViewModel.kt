package com.instriker.wcre.presentation

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt

import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.repositories.Card

class PileSummaryViewModel {
    val cardSummary = BindingsFactory.bindString()
    val pileNumber = BindingsFactory.bindInteger(0)
    val isOdd = BindingsFactory.bindBoolean()

    companion object {

        fun Create(cards: Array<Card>): PileSummaryViewModel {
            val pvm = PileSummaryViewModel()

            var cSum = StringBuilder()
            for (card in cards) {
                if (cSum.length > 0) {
                    cSum.append(" & ")
                }
                if (card.quantity > 1) {
                    cSum.append(String.format("%sx ", card.quantity))
                }
                cSum = cSum.append(card.name)
            }
            pvm.cardSummary.set(cSum.toString())

            return pvm
        }
    }
}