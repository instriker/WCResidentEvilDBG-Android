package com.instriker.wcre.generators

import com.instriker.wcre.repositories.Card

class EmptySelector : IPileSelector {

    override fun choosePiles(toChooseFrom: Collection<Array<Card>>): Array<Array<Card>> {
        return arrayOf<Array<Card>>()
    }
}
