package com.instriker.wcre.presentation

import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField

import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.generators.Options.ExtensionOptions
import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.CardType
import com.instriker.wcre.repositories.Cards
import com.instriker.wcre.repositories.GameExtension
import com.instriker.wcre.services.GameContentService

import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap

class NewGameTrackerSetupViewModel(serviceLocator: ServiceLocator) : RedbgcViewModel(serviceLocator) {
    val useTurnCounters = BindingsFactory.bindBoolean(false)
    val trackSkills = BindingsFactory.bindBoolean(false)
    val partnerMode = BindingsFactory.bindBoolean(false)
    val canStartGame = BindingsFactory.bindBoolean(false)

    val player1Card = BindingsFactory.bind<PickPlayerCardViewModel>()
    val player2Card = BindingsFactory.bind<PickPlayerCardViewModel>()
    val player3Card = BindingsFactory.bind<PickPlayerCardViewModel>()
    val player4Card = BindingsFactory.bind<PickPlayerCardViewModel>()

    init {
        init()
    }

    private fun init() {
        useTurnCounters.set(gameTrackerService.useTurnCounter)
        trackSkills.set(gameTrackerService.trackSkills)
        partnerMode.set(gameTrackerService.partnerMode)

        val characters = availableCharacters

        val isPartnerMode = partnerMode.get()
        player1Card.set(PickPlayerCardViewModel(this.serviceLocator, characters, 1, isPartnerMode))
        player2Card.set(PickPlayerCardViewModel(this.serviceLocator, characters, 2, isPartnerMode))
        player3Card.set(PickPlayerCardViewModel(this.serviceLocator, characters, 3, isPartnerMode))
        player4Card.set(PickPlayerCardViewModel(this.serviceLocator, characters, 4, isPartnerMode))

        saveUseTurnCountOnChanged()
        saveTrackSkillsOnChanged()
        savePartnerModeOnChanged()

        this.gameTrackerService.observeGameChanged { value -> canStartGame.set(gameTrackerService.validGameExists()!!) }
    }

    private val availableCharacters: ArrayList<CardViewModel>
        get() {
            val extensionsById = GameExtension.toMap(this.gameContentService.extensions)
            val extOptions = this.settingsService.extensionOptions

            val characters = ArrayList<CardViewModel>()

            val cardTypeNames = this.gameContentService.cardTypeNames
            val gameService = this.gameContentService
            val allCharacters = gameService.charactersCards
            val filteredCards = Cards.filterCardsByExtensions(Arrays.asList(*allCharacters), extOptions.fromExtensions).toTypedArray<Card>()

            Arrays.sort(filteredCards, CardComparators.createComparator(CardSortOrder.ByName))

            var pileIndex = 0
            for (card in filteredCards) {
                val svm = CardViewModel.Create(card, extensionsById, pileIndex, cardTypeNames, resourceServices)
                svm.showCardType.set(false)
                characters.add(svm)

                pileIndex++
            }
            return characters
        }

    private fun saveUseTurnCountOnChanged() {
        useTurnCounters.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                val newVal = (sender as ObservableBoolean).get()
                gameTrackerService.useTurnCounter = newVal
            }
        })
    }

    private fun saveTrackSkillsOnChanged() {
        trackSkills.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                val mustTrack = (sender as ObservableBoolean).get()
                gameTrackerService.trackSkills = mustTrack
            }
        })
    }

    private fun savePartnerModeOnChanged() {
        partnerMode.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                val usePartnerMode = (sender as ObservableBoolean).get()
                gameTrackerService.partnerMode = usePartnerMode
                player1Card.get()!!.setPartnerEnabled(usePartnerMode)
                player2Card.get()!!.setPartnerEnabled(usePartnerMode)
                player3Card.get()!!.setPartnerEnabled(usePartnerMode)
                player4Card.get()!!.setPartnerEnabled(usePartnerMode)
            }
        })
    }

    fun changeSettings() {
        this.activityService.navigateToSettings(false, false)
    }

    fun startGame() {
        activityService.navigateToGameTracker()
        activityService.finish()
    }

    override fun onSettingsChanged() {
        val characters = availableCharacters
        player1Card.get()?.let{ it.setCharacters(characters) }
        player2Card.get()?.let{ it.setCharacters(characters) }
        player3Card.get()?.let{ it.setCharacters(characters) }
        player4Card.get()?.let{ it.setCharacters(characters) }
    }
}
