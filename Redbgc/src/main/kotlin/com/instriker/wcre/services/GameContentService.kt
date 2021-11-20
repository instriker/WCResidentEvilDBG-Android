package com.instriker.wcre.services

import com.instriker.wcre.R
import java.util.*

import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.CardRepository
import com.instriker.wcre.repositories.CardType
import com.instriker.wcre.repositories.ExtensionRepository
import com.instriker.wcre.repositories.GameExtension
import com.instriker.wcre.repositories.GameMode
import com.instriker.wcre.repositories.Mansion
import com.instriker.wcre.repositories.MansionRepository
import com.instriker.wcre.repositories.Scenario
import com.instriker.wcre.repositories.ScenarioRepository
import com.instriker.wcre.repositories.StandardMutualyExclusiveCardRepository
import com.instriker.wcre.repositories.StandardPilesRepository

class GameContentService(private val _repositoryFactory: RepositoryFactory, private val _resourceService: ResourceServices) {
    private val cardRepository: CardRepository by lazy {
        _repositoryFactory.createCardRepository()
    }
    private val gameExtensionRepository: ExtensionRepository by lazy {
        _repositoryFactory.createExtensionRepository()
    }
    private val scenarioRepository: ScenarioRepository by lazy {
        _repositoryFactory.createScenarioRepository()
    }
    private val standardPilesRepository: StandardPilesRepository by lazy {
        _repositoryFactory.createStandardPilesRepository()
    }
    private val standardMutualyExclusiveCardRepository: StandardMutualyExclusiveCardRepository by lazy {
        _repositoryFactory.createStandardMutualyExclusiveCardRepository()
    }
    private val mansionRepository: MansionRepository by lazy {
        _repositoryFactory.createMansionRepository()
    }

    val extensions: Array<GameExtension>
        get() = gameExtensionRepository.extensions

    val cards: Array<Card>
        get() = cardRepository.getCards(null)

    fun getCards(ids: IntArray): Array<Card> {
        return cardRepository.getCards(ids)
    }

    fun getCard(id: Int): Card? {
        val cards = cardRepository.getCards(intArrayOf(id))
        return if (cards != null && cards.size > 0)
            cards[0]
        else
            null
    }

    val scenarioCards: Array<Card>
        get() {
            val types = arrayOf(CardType.Action, CardType.Ammunition, CardType.Item, CardType.Weapon)
            val ids = cardRepository.getCardIdsForType(types)
            return cardRepository.getCards(ids)
        }

    /* ,CardType.Bonus */ val mansionCards: Array<Card>
        get() {
            val types = arrayOf(CardType.Infected, CardType.InfectedBoss, CardType.MansionItem, CardType.Token, CardType.Event)
            val ids = cardRepository.getCardIdsForType(types)
            return cardRepository.getCards(ids)
        }

    fun getCustomInventoryCardsForCharacter(id: Int?): Array<Card> {
        return cardRepository.getCustomInventoryCardsForCharacter(id!!)
    }

    val charactersCards: Array<Card>
        get() {
            val types = arrayOf(CardType.Character)
            val ids = cardRepository.getCardIdsForType(types)
            return cardRepository.getCards(ids)
        }

    fun getCardPilesForScenario(id: Int): Array<Array<Card>> {
        return cardRepository.getCardPilesForScenario(id)
    }

    fun getCardsForMansion(id: Int): Array<Card> {
        return cardRepository.getCardsForMansion(id)
    }

    fun getBasicResourcesPiles(extensionId: Int): Array<Array<Card>> {
        return cardRepository.getBasicResourcesPiles(extensionId)
    }

    val eventCountByExtensions: Map<Int, Int>
        get() = cardRepository.getCardTypeCountByExtensions(CardType.Event)

    val infectedCountByExtensions: Map<Int, Int>
        get() = cardRepository.getCardTypeCountByExtensions(CardType.Infected)

    val tokensCountByExtensions: Map<Int, Int>
        get() = cardRepository.getCardTypeCountByExtensions(CardType.Token)

    val mansionItemsCountByExtensions: Map<Int, Int>
        get() = cardRepository.getCardTypeCountByExtensions(CardType.MansionItem)

    fun getTotalDistinctCardsCount(cardType: CardType): Int {
        return cardRepository.getTotalDistinctCardsCount(cardType)
    }

    val scenarios: Array<Scenario>
        get() = scenarioRepository.getScenarios(null, null, null, null)

    val mansions: Array<Mansion>
        get() = mansionRepository.getMansions(null)

    fun searchScenarios(query: String?, gameMode: GameMode?, skillSystem: Boolean?): Array<Scenario> {
        var query = query
        if (query != null) {
            query = query.trim { it <= ' ' }
        }
        return scenarioRepository.getScenarios(null, query, gameMode, skillSystem)
    }

    fun getScenario(scenarioId: Int): Scenario? {
        val scenarios = scenarioRepository.getScenarios(intArrayOf(scenarioId), null, null, null)
        return if (scenarios == null || scenarios.size == 0) null else scenarios[0]
    }

    fun getMansion(mansionId: Int): Mansion? {
        val mansions = mansionRepository.getMansions(intArrayOf(mansionId))
        return if (mansions == null || mansions.size == 0) null else mansions[0]
    }

    val standardPiles: Array<IntArray>
        get() = standardPilesRepository.list

    val standardMutualyExclusiveCard: Array<IntArray>
        get() = standardMutualyExclusiveCardRepository.standardMutualyExclusiveCard

    val cardTypeNames: HashMap<CardType, String>
        get() {
            val texts = _resourceService.getStringArray(R.array.cardTypes)

            val results = HashMap<CardType, String>()
            for (current in CardType.values()) {
                results.put(current, texts[current.ordinal])
            }
            return results
        }

}
