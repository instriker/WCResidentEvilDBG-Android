package com.instriker.wcre.presentation

import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt

import java.util.HashMap

import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.CardType
import com.instriker.wcre.repositories.GameExtension
import com.instriker.wcre.services.ResourceServices

open class CardViewModel(
        card: Card?,
        extensionsById: HashMap<Int, GameExtension>?,
        index: Int,
        cardTypeNames: HashMap<CardType, String>?,
        resourceServices: ResourceServices?) {

    val isNone = BindingsFactory.bindBoolean(false)
    val name = BindingsFactory.bindString()
    val cardNumber = BindingsFactory.bindString()
    val slotNumber = BindingsFactory.bind<Int>()
    val count = BindingsFactory.bindInteger()
    val cardType = BindingsFactory.bindString()
    val extensionName = BindingsFactory.bindString()
    val id = BindingsFactory.bindInteger()
    val extensionId = BindingsFactory.bindInteger()
    val hasSlotNumber = BindingsFactory.bindBoolean()
    val hasManyCards = BindingsFactory.bindBoolean()
    val cost = BindingsFactory.bind<Int>()
    val hasCost = BindingsFactory.bindBoolean(false)
    val isOdd = BindingsFactory.bindBoolean()
    val groupTitle = BindingsFactory.bindString()
    val hasGroupTitle = BindingsFactory.bindBoolean(false)
    var cardTypeId: CardType? = null
    val decorations = BindingsFactory.bind<Int>()
    val hasDecoration = BindingsFactory.bindBoolean(false)
    val xpCost = BindingsFactory.bind<Int>()
    val hasXpCost = BindingsFactory.bindBoolean(false)
    val showCardType = BindingsFactory.bindBoolean(true)
    val health = BindingsFactory.bind<Int>()
    val hasHealth = BindingsFactory.bindBoolean(false)
    val cardPhoto = BindingsFactory.bindInteger(0)

    init {
        if (card == null) {
            isNone.set(true)
        } else {
            this.slotNumber.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(observable: Observable, i: Int) {
                    hasSlotNumber.set(slotNumber.get() != null)
                }
            })

            this.count.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(observable: Observable, i: Int) {
                    hasManyCards.set(count.get() > 1)
                }
            })

            this.name.set(card!!.name)
            this.cardNumber.set(card.cardNumber)
            this.cardTypeId = card.cardType
            this.cardType.set(cardTypeNames!!.getValue(card.cardType))
            this.extensionId.set(card.extensionId)
            this.id.set(card.id)
            this.slotNumber.set(card.slotNumber)
            this.hasCost.set(card.cost != null)
            this.cost.set(card.cost)
            this.count.set(1)
            this.decorations.set(card.decorations)
            this.hasDecoration.set(card.decorations != null)
            this.xpCost.set(card.xpCost)
            this.hasXpCost.set(card.xpCost != null)
            this.health.set(card.health)
            this.hasHealth.set(card.health != null)
            this.cardPhoto.set(if (card.bigPicture != null) resourceServices!!.getDrawableId(card.bigPicture) else 0)

            this.extensionName.set(extensionsById!!.getValue(card.extensionId).name)
            this.isOdd.set(index % 2 != 0)
        }
    }

    companion object {
        val None = CardViewModel(null, null, 0, null, null)

        fun Create(card: Card, extensionsById: HashMap<Int, GameExtension>, index: Int, cardTypeNames: HashMap<CardType, String>, resourceServices: ResourceServices): CardViewModel {
            val vm = CardViewModel(card, extensionsById, index, cardTypeNames, resourceServices)
            return vm
        }
    }
}
