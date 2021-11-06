package com.instriker.wcre.repositories

import java.util.*

class Card(val id: Int,
           val cardNumber: String,
           val name: String,
           val cardType: CardType,
           val extensionId: Int,
           val slotNumber: Int?,
           val decorations: Int?,
           val weaponCategory: WeaponCategory?,
           var quantity: Int,
           val basicResourcePileGroup: Int?,
           val cost: Int?,
           val xpCost: Int?,
           val health: Int?,
           val bigPicture: String?,
           val damage: Int?,
           val allowExtraExplore: Boolean,
           val allowTrash: Boolean) : Cloneable {

    companion object {
        fun generateDeck(cardsSamples: Collection<Card>): ArrayList<Card> {
            val results = ArrayList<Card>()
            for (cardSample in cardsSamples) {
                for (i in 0..cardSample.quantity - 1) {
                    try {
                        val card = cardSample.clone() as Card
                        card.quantity = 1
                        results.add(card)
                    } catch (e: CloneNotSupportedException) {
                        // Won't throw.
                        e.printStackTrace()
                    }

                }
            }
            return results
        }
    }
}