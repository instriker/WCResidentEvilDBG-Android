package com.instriker.wcre.presentation

import java.util.Comparator

import com.instriker.wcre.repositories.Card


object CardComparators {
    fun createComparator(sortOrder: CardSortOrder): Comparator<Card>? {

        when (sortOrder) {

            CardSortOrder.ByType -> return Comparator { lhs, rhs ->
                var diff = lhs.cardType.ordinal - rhs.cardType.ordinal
                if (diff == 0) {
                    diff = lhs.name.compareTo(rhs.name)
                }
                if (diff == 0) {
                    diff = lhs.extensionId - rhs.extensionId
                }
                diff
            }


            CardSortOrder.ByName -> return Comparator { lhs, rhs ->
                var diff = lhs.name.compareTo(rhs.name)
                if (diff == 0) {
                    diff = lhs.extensionId - rhs.extensionId
                }
                diff
            }


            CardSortOrder.ByNumber -> return Comparator { lhs, rhs ->
                var diff = lhs.cardNumber.compareTo(rhs.cardNumber)
                if (diff == 0) {
                    diff = lhs.extensionId - rhs.extensionId
                }
                diff
            }


            else -> return null
        }
    }
}
