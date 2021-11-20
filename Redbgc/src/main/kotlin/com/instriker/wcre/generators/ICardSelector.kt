package com.instriker.wcre.generators

import com.instriker.wcre.repositories.Card

interface ICardSelector {
    fun chooseCards(toChooseFrom: Collection<Card>): Array<Card>
}