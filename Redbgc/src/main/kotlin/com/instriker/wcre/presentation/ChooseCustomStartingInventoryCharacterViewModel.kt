package com.instriker.wcre.presentation

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField

import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap

import com.instriker.wcre.framework.Bindings
import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.framework.IStateStore
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.generators.Options.ExtensionOptions
import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.CardType
import com.instriker.wcre.repositories.Cards
import com.instriker.wcre.repositories.GameExtension
import com.instriker.wcre.services.GameContentService

class ChooseCustomStartingInventoryCharacterViewModel(serviceLocator: ServiceLocator) : RedbgcViewModel(serviceLocator) {
    val characters = BindingsFactory.bindCollection<CardViewModel>()
    private var _currentSortMode = CardSortOrder.ByName

    init {

        refreshCharacters()
    }

    private fun refreshCharacters(cardSortOrder: CardSortOrder = _currentSortMode) {
        _currentSortMode = cardSortOrder
        val extensionsById = GameExtension.toMap(this.gameContentService.extensions)
        Bindings.setCollection(this.characters, getCharacters(extensionsById, cardSortOrder))
    }

    private fun getCharacters(extensionsById: HashMap<Int, GameExtension>, cardSortOrder: CardSortOrder): Collection<CardViewModel> {
        // http://nickcharlton.net/post/building-custom-android-listviews
        // http://developer.android.com/resources/tutorials/views/hello-tablelayout.html

        val extOptions = this.settingsService.extensionOptions

        val characters = ArrayList<CardViewModel>()

        val cardTypeNames = this.gameContentService.cardTypeNames
        val gameService = this.gameContentService
        val allCharacters = gameService.charactersCards
        val filteredCards = filterCharactersByExtensions(Arrays.asList(*allCharacters), extOptions.fromExtensions).toTypedArray<Card>()

        Arrays.sort(filteredCards, CardComparators.createComparator(cardSortOrder))

        var pileIndex = 0
        for (card in filteredCards) {
            val svm = CardViewModel.Create(card, extensionsById, pileIndex, cardTypeNames, resourceServices)
            svm.showCardType.set(false)
            characters.add(svm)

            pileIndex++
        }

        return characters
    }

    fun showCustomStartingInventory(vm: Any): Any {
        val card = vm as CardViewModel
        val titles = HashMap<Int, String>()
        titles[0] = card.name.get() ?: ""

        val customInventory = this.gameContentService.getCustomInventoryCardsForCharacter(card.id.get())
        this.activityService.navigateToGeneratedCardList(customInventory, true, titles, true)
        return Unit
    }

    private fun filterCharactersByExtensions(allCharacters: Collection<Card>, fromExtensions: Collection<Int>): Collection<Card> {
        return Cards.filterCardsByExtensions(allCharacters, fromExtensions)
    }

    fun changeSettings() {
        this.activityService.navigateToSettings(false, false)
    }

    @JvmOverloads fun sort(sortMode: CardSortOrder = CardSortOrder.AsRulebook) {
        refreshCharacters(sortMode)
    }

    override fun onSettingsChanged() {
        refreshCharacters()
    }

    override fun onSaveInstanceState(savedInstanceState: IStateStore) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt("_currentSortMode", _currentSortMode.ordinal)
    }

    override fun onRestoreInstanceState(savedInstanceState: IStateStore) {
        super.onRestoreInstanceState(savedInstanceState)
        _currentSortMode = CardSortOrder.values()[savedInstanceState.getInt("_currentSortMode", CardSortOrder.ByName.ordinal)]
    }
}