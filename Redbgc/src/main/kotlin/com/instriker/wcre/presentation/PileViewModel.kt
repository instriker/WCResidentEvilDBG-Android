package com.instriker.wcre.presentation

import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.CardType
import com.instriker.wcre.repositories.GameExtension
import com.instriker.wcre.services.ResourceServices
import java.util.*

class PileViewModel constructor(card: Card,
                                extensionsById: HashMap<Int, GameExtension>,
                                index: Int,
                                cardTypeNames: HashMap<CardType, String>,
                                resourceServices: ResourceServices)
    : CardViewModel(card, extensionsById, index, cardTypeNames, resourceServices) {

    val pileNumber = BindingsFactory.bindInteger(0)

    companion object {
        fun Create(card: Card,
                   extensionsById: HashMap<Int, GameExtension>,
                   index: Int,
                   cardTypeNames: HashMap<CardType, String>,
                   resourceServices: ResourceServices): PileViewModel {
            val vm = PileViewModel(card, extensionsById, index, cardTypeNames, resourceServices)
            return vm
        }
    }
}
