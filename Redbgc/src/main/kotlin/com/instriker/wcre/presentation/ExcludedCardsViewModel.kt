package com.instriker.wcre.presentation

import com.instriker.wcre.R
import java.util.ArrayList
import java.util.Arrays

import com.instriker.wcre.framework.Bindings
import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.GameExtension

abstract class ExcludedCardsViewModel protected constructor(serviceLocator: ServiceLocator) : RedbgcViewModel(serviceLocator) {
    val cards = BindingsFactory.bindCollection<SelectableCardViewModel>()

    init {

        Bindings.setCollection(this.cards, loadCards())
    }

    private fun loadCards(): Collection<SelectableCardViewModel> {

        val originalExcludedCards = excludedCards
        Arrays.sort(originalExcludedCards)

        val extensionsById = GameExtension.toMap(this.gameContentService.extensions)

        val cards = ArrayList<SelectableCardViewModel>()
        var availableCards = allAvailableCards

        availableCards = filterCardsByOwnedExtensions(availableCards)
        Arrays.sort(availableCards, CardComparators.createComparator(CardSortOrder.ByType))

        val cardTypeNames = this.gameContentService.cardTypeNames

        var index = 0
        var previousType: com.instriker.wcre.repositories.CardType? = null
        for (currentCard in availableCards) {
            val foundPosition = Arrays.binarySearch(originalExcludedCards, currentCard.id)
            val isExcluded = foundPosition > -1
            val vm = SelectableCardViewModel.Create(currentCard, extensionsById, index++, isExcluded, cardTypeNames, resourceServices)

            if (previousType == null || previousType != currentCard.cardType) {
                vm.groupTitle.set(cardTypeNames[currentCard.cardType])
                vm.hasGroupTitle.set(true)
            }

            cards.add(vm)
            previousType = currentCard.cardType
        }
        return cards
    }

    private fun filterCardsByOwnedExtensions(scenarioCards: Array<Card>): Array<Card> {
        val extensions = this.settingsService.extensionOptions.fromExtensions
        val results = ArrayList<Card>(scenarioCards.size)

        for (current in scenarioCards) {
            if (extensions.contains(current.extensionId)) {
                results.add(current)
            }
        }

        return results.toTypedArray<Card>()
    }

    override fun onBackPressed(): Boolean {
        saveIfChanged()
        return super.onBackPressed()
    }

    override fun onStop() {
        saveIfChanged()

        super.onStop()
    }

    private fun saveIfChanged() {
        val originalExcludedCards = excludedCards
        val newExcludedCards = selectedCards

        Arrays.sort(originalExcludedCards)
        Arrays.sort(newExcludedCards)

        if (!Arrays.equals(originalExcludedCards, newExcludedCards)) {
            this.saveChanges(newExcludedCards)
            this.activityService.setResultOK()
        } else {
            this.activityService.setResultCanceled()
        }
    }

    private val selectedCards: IntArray
        get() {
            val founds = ArrayList<Int>()

            for (current in Bindings.getItems(cards)) {
                if (current.isSelected.get()) {
                    founds.add(current.id.get())
                }
            }

            return founds.toTypedArray().toIntArray()
        }

    private fun saveChanges(newExcludedCards: IntArray) {
        try {
            saveExcludedCards(newExcludedCards)
        } catch (ex: Exception) {
            this.activityService.showMessage(this.resourceServices.getString(R.string.settingsSaveError))
            return
        }

        this.activityService.showMessage(this.resourceServices.getString(R.string.settingsSaved))
    }

    protected abstract val allAvailableCards: Array<Card>

    protected abstract val excludedCards: IntArray

    protected abstract fun saveExcludedCards(newExcludedCards: IntArray)
}
