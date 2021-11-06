package com.instriker.wcre.presentation

import com.instriker.wcre.generators.Options.MansionDeckOptions
import com.instriker.wcre.repositories.Card

class ExcludedMansionCardsViewModel(serviceLocator: ServiceLocator) : ExcludedCardsViewModel(serviceLocator) {

    protected override val excludedCards: IntArray
        get() = this.settingsService.mansionDeckOptions.excludedCards

    protected override val allAvailableCards: Array<Card>
        get() = this.gameContentService.mansionCards

    override fun saveExcludedCards(newExcludedCards: IntArray) {
        val options = this.settingsService.mansionDeckOptions
        options.excludedCards = newExcludedCards
        this.settingsService.mansionDeckOptions = options
    }
}
