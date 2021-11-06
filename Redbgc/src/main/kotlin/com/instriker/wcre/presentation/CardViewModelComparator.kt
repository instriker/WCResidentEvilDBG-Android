package com.instriker.wcre.presentation

import com.instriker.wcre.repositories.CardType
import java.util.Comparator

class CardViewModelComparator : Comparator<CardViewModel> {

    override fun compare(lhs: CardViewModel, rhs: CardViewModel): Int {
        var diff = (lhs.cardTypeId ?: CardType.Character).ordinal - (rhs.cardTypeId ?: CardType.Character).ordinal
        if (diff == 0) {
            diff = Integer.valueOf(lhs.extensionId.get())!!.compareTo(Integer.valueOf(rhs.extensionId.get()))
        }
        if (diff == 0) {
            // It's string comparison here... fix if it's ever an issue
            diff = (lhs.cardNumber.get() ?: "").compareTo(rhs.cardNumber.get() ?: "")
        }
        return diff
    }
}