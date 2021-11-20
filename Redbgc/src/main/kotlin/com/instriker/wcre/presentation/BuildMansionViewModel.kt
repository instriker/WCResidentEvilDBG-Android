package com.instriker.wcre.presentation

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.instriker.wcre.R

import java.util.ArrayList
import java.util.Arrays
import java.util.Comparator
import java.util.HashMap

import com.instriker.wcre.config.Constants
import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.generators.MansionCards
import com.instriker.wcre.generators.MansionDeckGenerator
import com.instriker.wcre.generators.RandomOfficialMansion
import com.instriker.wcre.generators.Options.ExtensionOptions
import com.instriker.wcre.generators.Options.MansionDeckOptions
import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.GameExtension
import com.instriker.wcre.repositories.Mansion

class BuildMansionViewModel(serviceLocator: ServiceLocator) : RedbgcViewModel(serviceLocator) {

    val isStandardMansion = BindingsFactory.bindBoolean(false)
    val displayDetails = BindingsFactory.bind<MansionViewModel?>()

    val isGenerateMode = BindingsFactory.bindBoolean(false)
    val isPickRandomMode = BindingsFactory.bindBoolean(false)
    val generationSuccess = BindingsFactory.bindBoolean(false)

    private var _mansionCards: Array<Card>? = null

    init {
        val mode = generationMode
        isGenerateMode.set(mode == GenerateMode)
        isPickRandomMode.set(mode == RandomPickMode)

        loadInitialMansion()
    }

    private fun loadInitialMansion() {
        if (isGenerateMode.get()) {
            generateNewMansion()
        } else {
            pickRandomMansion()
        }
    }

    fun pickRandomMansion() {
        isStandardMansion.set(true)

        val extOptions = this.settingsService.extensionOptions

        val generator = RandomOfficialMansion(gameContentService)
        try {
            val picked = generator.pickRandom(extOptions)

            if (picked != null) {
                setActiveMansion(picked.mansion, picked.cards)
            } else {
                setActiveMansion(null, null)
            }
        } catch (ex: Exception) {
            if (Constants.DEBUG) {
                ex.printStackTrace()
            }
            activityService.showMessage("An unexpected problem occurred while generating the mansion.")
        }

    }

    fun generateNewMansion() {
        isStandardMansion.set(false)

        val options = this.settingsService.mansionDeckOptions
        val extOptions = this.settingsService.extensionOptions

        val generator = MansionDeckGenerator(gameContentService)
        try {
            val mansionCards = generator.generate(extOptions, options)

            if (mansionCards != null && mansionCards.isNotEmpty()) {
                val mansion = Mansion(-1, resourceServices.getString(R.string.customMansion), null, -1, null)
                setActiveMansion(mansion, mansionCards)
            } else {
                setActiveMansion(null, null)
            }
        } catch (ex: Exception) {
            if (Constants.DEBUG) {
                ex.printStackTrace()
            }
            activityService.showMessage("An unexpected problem occurred while generating the mansion.")
        }

    }

    private fun setActiveMansion(mansionData: Mansion?, mansionCards: Array<Card>?) {
        val extensionsById = GameExtension.toMap(this.gameContentService.extensions)

        val succeeded = mansionData != null && mansionCards != null && mansionCards.size > 0
        if (succeeded) {
            val svm = MansionViewModel.Create(this.serviceLocator, mansionData!!, extensionsById)
            if (isGenerateMode.get()) {
                svm.hideCardsList.set(true)
            }

            svm.loadDetails(mansionCards!!) {
                displayDetails.set(svm)
                _mansionCards = mansionCards
            }
        } else {
            displayDetails.set(null)
        }

        generationSuccess.set(succeeded)
    }

    private val generationMode: String
        get() = this.activityService.getParameterString("Mode", RandomPickMode)

    fun changeSettings() {
        this.activityService.navigateToSettings(isGenerateMode.get(), false)
    }

    fun showCardList() {
        val mansionCards = _mansionCards;
        val mansionDetails = this.displayDetails.get();

        if (mansionCards == null || mansionDetails == null) {
            // If not ready, do not crash, the user will try again in 1 second...
            return
        }

        if (!this.isStandardMansion.get()) {
            sortResults(mansionCards)
        }

        // Group same cards into piles
        val cardsById = ArrayList<ArrayList<Card>>()
        var previousCard: Card? = null
        for (card in mansionCards) {
            if (previousCard == null || previousCard.id != card.id) {
                cardsById.add(ArrayList<Card>())
            }

            cardsById[cardsById.size - 1].add(card)
            previousCard = card
        }

        val result = cardsById
                .map { current -> current.toTypedArray() }
                .toTypedArray()

        val titles = HashMap<Int, String>()
        titles[0]=  mansionDetails.name.get() ?: ""

        this.activityService.navigateToGeneratedPileList(result, isStandardMansion.get(), titles, isStandardMansion.get())
    }

    private fun sortResults(mansionCards: Array<Card>) {
        // Sort cards to have the same cards consecutive
        Arrays.sort(mansionCards, Comparator<Card> { a, b ->
            val result = a.cardType.ordinal - b.cardType.ordinal
            if (result != 0)
                return@Comparator result
            a.id - b.id
        })
    }

    override fun onSettingsChanged() {
        loadInitialMansion()
    }

    companion object {
        const val GenerateMode = "generate"
        const val RandomPickMode = "random"
    }
}
