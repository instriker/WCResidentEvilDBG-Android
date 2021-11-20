package com.instriker.wcre.generators

import com.instriker.wcre.repositories.Card

interface IPileSelector {
    fun choosePiles(toChooseFrom: Collection<Array<Card>>): Array<Array<Card>>

}
