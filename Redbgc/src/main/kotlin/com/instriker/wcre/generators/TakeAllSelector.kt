package com.instriker.wcre.generators

import java.util.*

import com.instriker.wcre.repositories.Card

class TakeAllSelector : IPileSelector {

    override fun choosePiles(toChooseFrom: Collection<Array<Card>>): Array<Array<Card>> {
        val choosen = ArrayList<Array<Card>>(toChooseFrom.size)
        for (card in toChooseFrom) {
            choosen.add(card)
        }
        return choosen.toTypedArray<Array<Card>>()
    }
}
