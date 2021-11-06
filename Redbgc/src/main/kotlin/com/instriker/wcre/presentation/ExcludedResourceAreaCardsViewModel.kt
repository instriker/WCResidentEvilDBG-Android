package com.instriker.wcre.presentation

import com.instriker.wcre.generators.Options.ResourceAreaOptions
import com.instriker.wcre.repositories.Card

class ExcludedResourceAreaCardsViewModel(serviceLocator: ServiceLocator) : ExcludedCardsViewModel(serviceLocator) {

    protected override val excludedCards: IntArray
        get() = this.settingsService.resourceAreaOptions.excludedCards

    protected override val allAvailableCards: Array<Card>
        get() = this.gameContentService.scenarioCards

    override fun saveExcludedCards(newExcludedCards: IntArray) {
        val options = this.settingsService.resourceAreaOptions
        options.excludedCards = newExcludedCards
        this.settingsService.resourceAreaOptions = options
    }
}
