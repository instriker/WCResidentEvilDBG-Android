package com.instriker.wcre.generators

import java.util.ArrayList

import com.instriker.wcre.repositories.Card

abstract class BasePileSelector(private val _expectedCount: Int) : IPileSelector {

    override fun choosePiles(toChooseFrom: Collection<Array<Card>>): Array<Array<Card>> {
        val choosen = ArrayList<Array<Card>>(_expectedCount)
        var actualCount = 0

        if (_expectedCount > 0) {
            for (pile in toChooseFrom) {
                if (isValidPile(pile)) {
                    choosen.add(pile)

                    actualCount++
                    if (actualCount == _expectedCount) {
                        break
                    }
                }
            }
        }

        return choosen.toTypedArray<Array<Card>>()
    }

    protected abstract fun isValidPile(pile: Array<Card>): Boolean
}
