package com.instriker.wcre.presentation

import androidx.databinding.ObservableBoolean

import java.util.HashMap

import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.CardType
import com.instriker.wcre.repositories.GameExtension
import com.instriker.wcre.services.ResourceServices

class SelectableCardViewModel protected constructor(card: Card, extensionsById: HashMap<Int, GameExtension>, index: Int, cardTypeNames: HashMap<CardType, String>, resourceServices: ResourceServices) : CardViewModel(card, extensionsById, index, cardTypeNames, resourceServices) {
    val isSelected = BindingsFactory.bindBoolean(false)

    companion object {

        fun Create(card: Card, extensionsById: HashMap<Int, GameExtension>, index: Int, selected: Boolean, cardTypeNames: HashMap<CardType, String>, resourceServices: ResourceServices): SelectableCardViewModel {
            val vm = SelectableCardViewModel(card, extensionsById, index, cardTypeNames, resourceServices)
            vm.isSelected.set(selected)
            return vm
        }
    }
}
