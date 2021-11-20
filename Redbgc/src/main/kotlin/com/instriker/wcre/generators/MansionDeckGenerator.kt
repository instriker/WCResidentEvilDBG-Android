package com.instriker.wcre.generators

import java.util.ArrayList
import java.util.Arrays

import com.instriker.wcre.framework.Randomizer
import com.instriker.wcre.generators.Options.ExtensionOptions
import com.instriker.wcre.generators.Options.MansionDeckInfectedOptions
import com.instriker.wcre.generators.Options.MansionDeckOptions
import com.instriker.wcre.generators.Options.RangeOptions
import com.instriker.wcre.repositories.Card
import com.instriker.wcre.repositories.CardType
import com.instriker.wcre.services.GameContentService

class MansionDeckGenerator(private val _gameContentService: GameContentService) {

    @Throws(Exception::class)
    fun generate(extOptions: ExtensionOptions, options: MansionDeckOptions): Array<Card> {
        var allCards = mansionDeckCards
        filterExtensions(allCards, extOptions.fromExtensions)
        filterExcludedCards(allCards, options.excludedCards)

        allCards = Card.generateDeck(allCards)
        shuffleCards(allCards)

        val results = ArrayList<Card>()

        val bossSelector = getBossSelector(options)
        val eventsSelector = getEventsSelector(options)
        val tokensSelector = getTokensSelector(options)
        val itemsSelector = getItemsSelector(options)
        val infectedSelector = getInfectedSelector(options)

        chooseCards(allCards, results, bossSelector.chooseCards(allCards))
        chooseCards(allCards, results, eventsSelector.chooseCards(allCards))
        chooseCards(allCards, results, tokensSelector.chooseCards(allCards))
        chooseCards(allCards, results, itemsSelector.chooseCards(allCards))
        chooseCards(allCards, results, infectedSelector.chooseCards(allCards))

        return results.toTypedArray<Card>()
    }

    private val mansionDeckCards: ArrayList<Card>
        get() {
            val allCards = ArrayList<Card>()
            val cards = _gameContentService.cards
            for (card in cards) {
                if (isMansionDeckCard(card)) {
                    allCards.add(card)
                }
            }
            return allCards
        }

    private fun isMansionDeckCard(card: Card): Boolean {
        return card.cardType == CardType.Infected
                || card.cardType == CardType.InfectedBoss
                || card.cardType == CardType.Event
                || card.cardType == CardType.Token
                || card.cardType == CardType.Bonus
                || card.cardType == CardType.MansionItem
    }

    private fun filterExtensions(allCards: ArrayList<Card>, fromExtensions: Collection<Int>) {
        for (index in allCards.indices.reversed()) {
            val current = allCards[index]
            if (!fromExtensions.contains(current.extensionId)) {
                allCards.removeAt(index)
            }
        }
    }

    private fun filterExcludedCards(allCards: ArrayList<Card>, excludedCards: IntArray) {
        if (excludedCards.size == 0) {
            return
        }

        Arrays.sort(excludedCards)

        for (index in allCards.indices.reversed()) {
            val current = allCards[index]

            val position = Arrays.binarySearch(excludedCards, current.id)
            if (position >= 0) {
                allCards.removeAt(index)
            }
        }
    }

    private fun shuffleCards(allCards: ArrayList<Card>) {
        val allCardsRandomized = ArrayList<Card>()
        while (allCards.size > 0) {
            val index = Randomizer.getRandom(1, allCards.size) - 1
            val choosen = allCards[index]
            allCardsRandomized.add(choosen)
            allCards.removeAt(index)
        }
        allCards.addAll(allCardsRandomized)
    }

    private fun chooseCards(allCards: ArrayList<Card>, results: ArrayList<Card>, toAdd: Array<Card>) {
        for (current in toAdd) {
            results.add(current)
            allCards.remove(current)
        }
    }

    private fun getBossSelector(options: MansionDeckOptions): ICardSelector {
        if (options.bossOptions.isRandom) {
            return RandomBossSelector()
        } else {
            return BossByExtensionSelector(options.bossOptions.fromExtensionId)
        }
    }

    private fun getEventsSelector(options: MansionDeckOptions): com.instriker.wcre.generators.ICardSelector {
        val opt = options.eventsOptions
        val cardCount = Randomizer.getRandom(opt.minimumItems, opt.maximumItems)
        return RandomCardSelector(cardCount, CardType.Event)
    }

    private fun getItemsSelector(options: MansionDeckOptions): com.instriker.wcre.generators.ICardSelector {
        val opt = options.itemsOptions
        val cardCount = Randomizer.getRandom(opt.minimumItems, opt.maximumItems)
        return RandomCardSelector(cardCount, CardType.MansionItem)
    }

    private fun getTokensSelector(options: MansionDeckOptions): com.instriker.wcre.generators.ICardSelector {
        val opt = options.tokensOptions
        val cardCount = Randomizer.getRandom(opt.minimumItems, opt.maximumItems)
        return RandomCardSelector(cardCount, CardType.Token)
    }

    private fun getInfectedSelector(options: MansionDeckOptions): com.instriker.wcre.generators.ICardSelector {
        val opt = options.infectedOptions
        val cardCount = Randomizer.getRandom(opt.totalCount.minimumItems, opt.totalCount.maximumItems)
        return RandomCardSelector(cardCount, CardType.Infected)
    }
}