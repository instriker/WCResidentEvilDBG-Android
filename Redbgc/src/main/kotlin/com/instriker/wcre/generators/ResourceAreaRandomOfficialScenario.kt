package com.instriker.wcre.generators

import java.util.ArrayList
import java.util.Arrays

import com.instriker.wcre.framework.Randomizer
import com.instriker.wcre.generators.Options.*
import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.CardType
import com.instriker.wcre.repositories.Scenario
import com.instriker.wcre.repositories.Scenarios
import com.instriker.wcre.services.*

class ResourceAreaRandomOfficialScenario(private val _gameContentService: GameContentService) {

    @Throws(Exception::class)
    fun pickRandom(extOptions: ExtensionOptions): ScenarioResourceArea? {
        val allCards = resourceAreaCards
        filterExtensions(allCards, extOptions.fromExtensions)
        val allPiles = generatePiles(allCards)

        val allScenario = filterScenariosByExtensions(scenarios, extOptions.fromExtensions)
        val takenScenario = pickRandomScenario(allScenario)

        val results = ArrayList<Array<Card>>()
        if (takenScenario == null) {
            return null
        }

        chooseCards(allPiles, results, getBasicResourcesSelector(takenScenario).choosePiles(allPiles))
        chooseCards(allPiles, results, TakeAllSelector().choosePiles(getScenarioPiles(takenScenario)))

        return ScenarioResourceArea(takenScenario, results.toTypedArray<Array<Card>>())
    }

    private fun generatePiles(allCards: ArrayList<Card>): ArrayList<Array<Card>> {
        val allPiles = ArrayList<Array<Card>>()

        if (true) {
            val standardPiles = _gameContentService.standardPiles
            for (currentPile in standardPiles) {

                val pile = ArrayList<Card>(currentPile.size)
                for (cardId in currentPile) {
                    for (card in allCards) {
                        if (card.id == cardId) {
                            pile.add(card)
                            allCards.remove(card)
                            break
                        }
                    }
                }

                if (!pile.isEmpty()) {
                    allPiles.add(pile.toTypedArray<Card>())
                }
            }
        }

        for (card in allCards) {
            allPiles.add(arrayOf(card))
        }

        return allPiles
    }

    private val resourceAreaCards: ArrayList<Card>
        @Throws(Exception::class)
        get() {
            val allCards = ArrayList<Card>()
            val cards = _gameContentService.cards ?: throw Exception("Could not read game card infos.")

            for (card in cards) {
                if (isResourceAreaCard(card)) {
                    allCards.add(card)
                }
            }
            return allCards
        }

    private fun isResourceAreaCard(card: Card): Boolean {
        return card.cardType == CardType.Ammunition
                || card.cardType == CardType.Weapon
                || card.cardType == CardType.Item
                || card.cardType == CardType.Action
    }

    private fun pickRandomScenario(allScenario: List<Scenario>): Scenario? {
        if (allScenario.size == 0) {
            return null
        }
        val index = Randomizer.getRandom(0, allScenario.size - 1)
        return allScenario[index]
    }

    private val scenarios: List<Scenario>
        @Throws(Exception::class)
        get() {
            val scenarios = _gameContentService.scenarios ?: throw Exception("Could not read scenarios.")

            return Arrays.asList(*scenarios)
        }

    private fun filterExtensions(allCards: ArrayList<Card>, fromExtensions: Collection<Int>) {
        for (index in allCards.indices.reversed()) {
            val current = allCards[index]
            if (!fromExtensions.contains(current.extensionId)) {
                allCards.removeAt(index)
            }
        }
    }

    private fun filterScenariosByExtensions(allScenarios: Collection<Scenario>, fromExtensions: Collection<Int>): List<Scenario> {
        return Scenarios.filterScenariosByExtensions(allScenarios, fromExtensions)
    }

    private fun getBasicResourcesSelector(scenario: Scenario): IPileSelector {
        if (scenario.useBasicResources) {
            return BasicResourcesSelector()
        } else {
            return EmptySelector()
        }
    }

    private fun getScenarioPiles(takenScenario: Scenario): Collection<Array<Card>> {
        val scenarioPiles = this._gameContentService.getCardPilesForScenario(takenScenario.id)
        val result = ArrayList<Array<Card>>(scenarioPiles.size)
        for (current in scenarioPiles) {
            result.add(current)
        }
        return result
    }

    private fun chooseCards(allCards: ArrayList<Array<Card>>, results: ArrayList<Array<Card>>, toAdd: Array<Array<Card>>) {
        for (current in toAdd) {
            results.add(current)
            allCards.remove(current)
        }
    }
}